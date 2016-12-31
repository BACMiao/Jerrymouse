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
    private int count;              //最后一个有效字符在缓冲区的位置，即缓冲区的字符数
    private int pos;                //指定字符在缓冲区的位置

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
     * @param httpRequestLine
     * @return
     */
    public HttpRequestLine readRequestLine(HttpRequestLine httpRequestLine) throws IOException {
        if (httpRequestLine.methodEnd != 0) {
            httpRequestLine.recycle();
        }
        //检查空白行
        int chr = 0;
        do {
            //跳过CR或者LF
            try {
                chr = read();
            } catch (IOException e) {
                chr = -1;
            }
        } while ((chr == CR) || (chr == LF));
        if (chr == -1) {
            throw new EOFException("请求流的读取行错误！"); //todo
        }
        pos--;

        //读取方法的名字
        int maxRead = httpRequestLine.method.length;  //方法的长度
        int readStart = pos;                          //开始读取的位置
        int readCount = 0;

        boolean space = false;

        while (!space) {
            if (readCount >= maxRead) {
                if ((2 * maxRead) <= HttpRequestLine.MAX_METHOD_SIZE) {
                    char[] newBuffer = new char[2 * maxRead];
                    System.arraycopy(httpRequestLine.method, 0, newBuffer, 0,
                            maxRead);
                    httpRequestLine.method = newBuffer;
                    maxRead = httpRequestLine.method.length;
                } else {
                    throw new IOException("请求行太长！"); //todo
                }
            }

            if (pos >= count) {
                int val = read();
                if (val == -1){
                    throw new IOException("请求流的读取行错误！"); //todo
                }
                pos = 0;
                readStart = 0;
            }
            if (buffer[pos] == SP){
                space = true;
            }
            httpRequestLine.method[readCount] = (char) buffer[pos];
            readCount++;
            pos++;
        }

        httpRequestLine.methodEnd = readCount - 1;

        return null;
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
     * @return
     * @throws IOException
     */
    @Override
    public int read() throws IOException {
        if (pos >= count) {
            fill();
            if (pos >= count) {
                return -1;
            }
        }
        //将byte转换为int todo
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
