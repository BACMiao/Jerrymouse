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
    private int pos;                //当前指针在缓冲区的位置

    private static final byte CR = (byte) '\r';
    private static final byte LF = (byte) '\n';
    private static final byte SP = (byte) ' ';

    /**
     * @param inputStream 套接字的输入流
     * @param bufferSize  内部缓冲区大小
     */
    public SocketInputStream(InputStream inputStream, int bufferSize) {
        this.inputStream = inputStream;
        buffer = new byte[bufferSize];
    }

    /**
     * 返回HTTP的第一行的内容，即包含URI、请求方法和HTTP版本信息
     * 读取请求行，并复制到指定的缓冲区
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
        int readStart = pos;                          //开始读取的位置
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
            if (pos >= count) {
                int val = read();
                if (val == -1) {
                    throw new IOException("请求流的行读取错误！");
                }
                pos = 0;
                readStart = 0;
            }
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
        readStart = pos;                          //开始读取的位置
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

            if (pos >= count) {
                int val = read();
                if (val == -1) {
                    throw new IOException("请求流的行读取错误！");
                }
                pos = 0;
                readStart = 0;
            }
            if (buffer[pos] == SP) {
                space = true;
            } else if (buffer[pos] == CR || buffer[pos] == LF) {
                // HTTP/0.9风格的请求
                eol = true;
                space = true;
            }
            httpRequestLine.uri[readCount] = (char) buffer[pos];
            readCount++;
            pos++;
        }
        httpRequestLine.uriEnd = readCount - 1;

        //读取协议

        maxRead = httpRequestLine.protocol.length;
        readStart = pos;
        readCount = 0;

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

            if (pos >= count) {
                int val = read();
                if (val == -1) {
                    throw new IOException("请求流的行读取错误！");
                }
                pos = 0;
                readStart = 0;
            }
            if (buffer[pos] == CR) {

            } else if (buffer[pos] == LF) {
                eol = true;
            } else {
                httpRequestLine.protocol[readCount] = (char) buffer[pos];
                readCount++;
            }
            pos++;
        }

        httpRequestLine.protocolEnd = readCount;
    }

    /**
     * 读取所有请求头信息
     *
     * @return
     */
    public HttpHeader readHeader() {
        return null;
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
}
