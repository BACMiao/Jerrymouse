package com.bapocalypse.Jerrymouse.connector.http;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @package: com.bapocalypse.Jerrymouse.connector.http
 * @Author: 陈淼
 * @Date: 2016/12/21
 * @Description: SocketInputStream类从InputStream对象中读取字节流，
 * SocketInputStream是InputStream的包装类。
 */
public class SocketInputStream extends InputStream {
    private InputStream inputStream;
    private byte[] buffer;
    private int count;              //最后一个有效字节在缓冲区的位置，即缓冲区的字节数
    private int pos;                //当前指针在缓冲区的位置（索引）

    private static final byte CR = (byte) '\r';
    private static final byte LF = (byte) '\n';
    private static final byte SP = (byte) ' ';
    private static final byte COLON = (byte) ':';
    private static final int LC_OFFSET = 'A' - 'a';
    private static final byte HT = (byte) '\t';

    /**
     * @param inputStream 套接字的输入流
     * @param bufferSize  内部缓冲区大小
     */
    public SocketInputStream(InputStream inputStream, int bufferSize) {
        this.inputStream = inputStream;
        buffer = new byte[bufferSize];
    }

    /**
     * 解析HTTP的第一行的内容，即包含URI、请求方法和HTTP版本信息，并复制到指定的缓冲区
     * 以及给HttpRequestLine中的方法数组，uri数组、协议数组等变量赋值
     *
     * @param httpRequestLine HTTP请求行的解析，用于判断指定字符串在请求行中是否存在
     * @throws IOException 会抛出IO异常
     */
    public void readRequestLine(HttpRequestLine httpRequestLine) throws IOException {
        if (httpRequestLine.methodEnd != 0) {
            httpRequestLine.recycle();
        }
        //检查空白行，即跳过开头的CR或者LF
        int chr = 0;
        do {
            try {
                chr = read();
            } catch (IOException e) {
                chr = -1;
            }
        } while ((chr == CR) || (chr == LF));
        if (chr == -1) {
            throw new EOFException("请求流的行读取错误！");
        }
        //因为read()方法中，返回字节所对应的int值之后，pos都会加1，
        // 例如：第n个字节为非空白行，chr = read()后，pos=n+1才跳出循环，所以需要减1得到第一个字节的索引
        pos--;

        //读取方法
        int maxRead = httpRequestLine.method.length;  //方法数组的初始长度（8）
        int readStart = pos;                          //开始读取的位置（索引）
        int readCount = 0;                            //读取的字节数量
        boolean space = false;                        //钩子，用于启动和暂停循环

        while (!space) {
            //如果缓冲区满了，扩大缓冲区
            if (readCount >= maxRead) {
                if ((2 * maxRead) <= HttpRequestLine.MAX_METHOD_SIZE) {
                    char[] newBuffer = new char[2 * maxRead];
                    //从指定源数组（第一个参数）中复制一个数组，复制从指定的位置开始（srcPos），
                    // 到目标数组（第三个参数）的指定位置结束（第五个参数为复制的数量）。
                    System.arraycopy(httpRequestLine.method, 0, newBuffer, 0,
                            maxRead);
                    httpRequestLine.method = newBuffer;
                    maxRead = httpRequestLine.method.length;
                } else {
                    throw new IOException("请求的方法太长！");
                }
            }

            //在内部缓冲区的末端
            resetIndex(readStart);

            //如果读取到空格，则停止循环
            if (buffer[pos] == SP) {
                space = true;
            }
            httpRequestLine.method[readCount] = (char) buffer[pos];
            readCount++;
            pos++;
        }
        //去除最后一个空格
        httpRequestLine.methodEnd = readCount - 1;

        //读取URI
        maxRead = httpRequestLine.uri.length;     //URI数组的初始长度（64）
        readStart = pos;                          //开始读取的位置（索引）
        readCount = 0;                            //读取的字节数量
        space = false;                            //钩子，用于启动和暂停循环
        boolean eol = false;

        while (!space) {
            if (readCount >= maxRead) {
                if ((2 * maxRead) <= HttpRequestLine.MAX_URI_SIZE) {
                    char[] newBuffer = new char[2 * maxRead];
                    System.arraycopy(httpRequestLine.uri, 0, newBuffer, 0,
                            maxRead);
                    httpRequestLine.uri = newBuffer;
                    maxRead = httpRequestLine.uri.length;
                } else {
                    throw new IOException("请求的URI太长");
                }
            }

            resetIndex(readStart);

            if (buffer[pos] == SP) {
                space = true;
            } else if (buffer[pos] == CR || buffer[pos] == LF) {
                // HTTP/0.9风格的请求
                throw new IllegalStateException("已不支持此协议");
            }
            httpRequestLine.uri[readCount] = (char) buffer[pos];
            readCount++;
            pos++;
        }
        httpRequestLine.uriEnd = readCount - 1;

        //读取协议
        maxRead = httpRequestLine.protocol.length; //协议数组的初始长度（8）
        readStart = pos;                           //开始读取的位置
        readCount = 0;                             //读取的字节数量

        while (!eol) {
            if (readCount >= maxRead) {
                if ((2 * maxRead) <= HttpRequestLine.MAX_PROTOCOL_SIZE) {
                    char[] newBuffer = new char[2 * maxRead];
                    System.arraycopy(httpRequestLine.protocol, 0, newBuffer, 0,
                            maxRead);
                    httpRequestLine.protocol = newBuffer;
                    maxRead = httpRequestLine.protocol.length;
                } else {
                    throw new IOException("请求的协议太长");
                }
            }

            resetIndex(readStart);

            if (buffer[pos] != CR) {
                if (buffer[pos] == LF) {
                    eol = true;
                } else {
                    httpRequestLine.protocol[readCount] = (char) buffer[pos];
                    readCount++;
                }
            }
            pos++;
        }
        //去除最后一个空格
        httpRequestLine.protocolEnd = readCount;
    }

    /**
     * 解析HTTP的请求首部的名/值对，并复制到指定的缓冲区，
     * 给HttpHeader中的键名数组，键值数组等变量赋值
     *
     * @param header HTTP请求首部字段分解和判定的对象
     * @throws IOException 抛出IO读写异常
     */
    public void readHeader(HttpHeader header) throws IOException {
        if (header.nameEnd != 0) {
            header.recycle();
        }
        //检查空白行，即跳过请求行与请求首部字段之间的空白行
        int chr = read();
        if ((chr == CR) || (chr == LF)) {
            if (chr == CR) {
                chr = read();
            }
            header.nameEnd = 0;
            header.valueEnd = 0;
            return;
        } else {
            pos--;
        }

        //读取HTTP请求首部字段信息的键名
        int maxRead = header.name.length;    //请求首部信息键名数组的初始长度（32）
        int readStart = pos;                 //开始读取的位置
        int readCount = 0;                   //读取字节数量
        boolean colon = false;               //钩子，用于启动和暂停循环

        while (!colon) {
            //如果缓冲区满了，扩大缓冲区
            if (readCount >= maxRead) {
                if ((2 * maxRead) <= HttpHeader.MAX_NAME_SIZE) {
                    char[] newBuffer = new char[2 * maxRead];
                    System.arraycopy(header.name, 0, newBuffer, 0,
                            maxRead);
                    header.name = newBuffer;
                    maxRead = header.name.length;
                } else {
                    throw new IOException("请求首部字段的键名太长！");
                }
            }

            resetIndex(readStart);

            //碰到分号，即暂停循环
            if (buffer[pos] == COLON) {
                colon = true;
            }
            char val = (char) buffer[pos];
            //将大写的字母转为小写字母
            if (val >= 'A' && val <= 'Z') {
                val = (char) (val - LC_OFFSET);
            }
            header.name[readCount] = val;
            readCount++;
            pos++;
        }
        //去除最后一个空格
        header.nameEnd = readCount - 1;

        //读取HTTP请求首部字段信息的键值，可跨多行读取
        maxRead = header.value.length; //请求首部信息键值数组的初始长度（64）
        readStart = pos;               //开始读取的位置
        readCount = 0;                 //读取字节数量

        boolean eol = false;          //钩子，用于启动和暂停循环
        boolean validLine = true;     //钩子，用于启动和暂停循环

        while (validLine) {
            boolean space = true;     //钩子，用于启动和暂停循环
            while (space) {
                resetIndex(readStart);
                //跳过空格和TAB键
                if (buffer[pos] == SP || buffer[pos] == HT) {
                    pos++;
                } else {
                    space = false;
                }
            }

            while (!eol) {
                //如果缓冲区满了，扩大缓冲区
                if (readCount >= maxRead) {
                    if ((2 * maxRead) <= HttpHeader.MAX_VALUE_SIZE) {
                        char[] newBuffer = new char[2 * maxRead];
                        System.arraycopy(header.value, 0, newBuffer, 0,
                                maxRead);
                        header.value = newBuffer;
                        maxRead = header.value.length;
                    } else {
                        throw new IOException("请求首部字段的键值太长");
                    }
                }

                resetIndex(readStart);

                if (buffer[pos] != CR) {
                    //如果遇到换行符，则跳出循环
                    if (buffer[pos] == LF) {
                        eol = true;
                    } else {
                        //fixme 检查二进制转换机制是否正常
                        int ch = buffer[pos] & 0xff;
                        header.value[readCount] = (char) ch;
                        readCount++;
                    }
                }
                pos++;
            }

            int nextChr = read();

            //跨行读取，如果下一个字符不是空格或者TAB，读取键值结束
            if ((nextChr != SP) && (nextChr != HT)) {
                pos--;
                validLine = false;
            } else {
                eol = false;
                if (readCount >= maxRead) {
                    if ((2 * maxRead) <= HttpHeader.MAX_VALUE_SIZE) {
                        char[] newBuffer = new char[2 * maxRead];
                        System.arraycopy(header.value, 0, newBuffer, 0,
                                maxRead);
                        header.value = newBuffer;
                        maxRead = header.value.length;
                    } else {
                        throw new IOException("请求首部字段的键值太长");
                    }
                }
                header.value[readCount] = ' ';
                readCount++;
            }
        }
        header.valueEnd = readCount;
    }

    /**
     * 读取字节
     *
     * @return 下标为pos的元素字节所对应的int值
     * @throws IOException 抛出IO异常
     */
    @Override
    public int read() throws IOException {
        //只有第一次才执行这个判断
        if (pos >= count) {
            fill();
            if (pos >= count) {
                return -1;
            }
        }
        //将byte转换为int，先进行&运算，再++
        return buffer[pos++] & 0xff;
    }

    /**
     * 将从套接字得到的输入流存入到内部分配的缓冲区中
     *
     * @throws IOException 会抛出IO异常
     */
    private void fill() throws IOException {
        pos = 0;
        count = 0;
        int nRead = inputStream.read(buffer, 0, buffer.length);
        if (nRead > 0) {
            count = nRead;
        }
    }

    /**
     * 如果pos已经到达内部缓冲区的末端，则重置pos和readStart
     *
     * @param readStart 开始读取的位置（索引）
     * @throws IOException 抛出IO读写异常
     */
    private void resetIndex(int readStart) throws IOException {
        if (pos >= count) {
            int val = read();
            if (val == -1) {
                throw new IOException("请求流的行读取错误！");
            }
            pos = 0;
            readStart = 0;
        }
    }
}
