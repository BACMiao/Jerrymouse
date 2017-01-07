package com.bapocalypse.Jerrymouse.request;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @package: com.bapocalypse.Jerrymouse.request
 * @Author: 陈淼
 * @Date: 2017/1/2
 * @Description: HTTP请求流
 */
public class RequestStream extends ServletInputStream {
    private boolean closed = false;     //这个流是否被关闭
    private int count = 0;              //当前流的已经被读入到数组的字节数，即已经读入的字节数（递增的），不能大于报文主体的长度
    private int length = -1;            //报文主体长度，-1表示还未定义
    private InputStream inputStream;    //输入流，用以保存request中的请求输入流

    RequestStream(HttpRequestBase request) {
        super();
        closed = false;
        count = 0;
        length = request.getContentLength();
        inputStream = request.getStream();
    }

    /**
     * 从输入流中读取数据的下一个字节，并返回 0 到 255 范围内的 int 字节值。
     *
     * @return 返回下一个数据字节（0 到 255 范围内的 int 字节值）；如果到达流的末尾，则返回 -1。
     * @throws IOException 抛出IO读写异常
     */
    @Override
    public int read() throws IOException {
        if (closed) {
            throw new IOException("请求流已经关闭！");
        }
        //已经读入到流的末尾
        if (length >= 0 && count >= length) {
            return -1;
        }
        //读取下一个字节，返回其int值
        int b = inputStream.read();
        if (b >= 0) {
            count++;
        }
        return b;
    }

    /**
     * 将输入流中的信息读入到指定的缓冲区，返回读入缓冲区的字节数
     *
     * @param b 将被读入数据的缓冲区数组
     * @return 返回读入缓冲区的字节数目，若已经到了流的结尾，则返回-1
     * @throws IOException 抛出IO读写异常
     */
    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    /**
     * 将输入流中的信息读入到指定的缓冲区，返回读入缓冲区的字节数
     *
     * @param b   将被读入数据的缓冲区数组
     * @param off 数组b开始读入的位置
     * @param len 读入的最大长度
     * @return 返回读入缓冲区的字节数目，若已经到了流的结尾，则返回-1
     * @throws IOException 抛出IO读写异常
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int toRead = len;      //将要读入缓冲区的长度
        if (length > 0) {
            //当前流的读入字节数大于报文长度，说明已读到了流的末尾
            if (count >= length) {
                return -1;
            }
            //当前读入的字节数+将要读入的长度大于报文长度
            if (count + len > length) {
                toRead = length - count;
            }
        }
        return super.read(b, off, toRead);
    }

    /**
     * 关闭此输入流并释放与该流关联的所有系统资源。
     *
     * @throws IOException 抛出IO读写异常
     */
    @Override
    public void close() throws IOException {
        if (closed) {
            throw new IOException("请求流已经关闭！");
        }
        if (length > 0) {
            while (count < length) {
                //释放输入流中的资源
                int b = read();
                if (b < 0) {
                    break;
                }
            }
        }
        closed = true;
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setReadListener(ReadListener readListener) {

    }
}
