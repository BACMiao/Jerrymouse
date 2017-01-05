package com.bapocalypse.Jerrymouse.response;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.IOException;

/**
 * @package: com.bapocalypse.Jerrymouse.response
 * @Author: 陈淼
 * @Date: 2017/1/4
 * @Description: HTTP响应流
 */
public class ResponseStream extends ServletOutputStream {
    private boolean closed = false;        //这个流是否被关闭
    private boolean commit = false;        //当缓冲刷新的时候我们是否应该作出相应响应
    private int count = 0;                 //被写入当前流的字节数（递增的），不能大于报文主体的长度
    private int length = -1;               //报文主体长度，-1表示还未定义
    private HttpResponse response = null;  //输入流相关response

    public ResponseStream(HttpResponse response) {
        super();
        closed = false;
        commit = false;
        count = 0;
        this.response = response;
    }

    /**
     * 关闭这个输出流，缓冲区的数据将被刷新输出，后续的数据在写入将会抛出异常
     *
     * @throws IOException 抛出IO读写异常
     */
    @Override
    public void close() throws IOException {
        if (closed) {
            throw new IOException("输入流已关闭！");
        }
        response.flushBuffer();
        closed = true;
    }

    /**
     * 刷新缓冲区，并将缓冲区中已经存在的数组输出
     *
     * @throws IOException 抛出IO读写异常
     */
    @Override
    public void flush() throws IOException {
        if (closed) {
            throw new IOException("输入流已关闭！");
        }
        if (commit) {
            response.flushBuffer();
        }
    }

    /**
     * 将指定的字节提交给http响应对象并写入输出流
     *
     * @param b 需要被写入的字节
     * @throws IOException 抛出IO读写异常
     */
    @Override
    public void write(int b) throws IOException {
        if (closed) {
            throw new IOException("输入流已被关闭！");
        }
        if (length > 0 && count >= length) {
            throw new IOException("不能写入比报文主体更多的字节数！");
        }
        response.write(b);
        count++;
    }

    /**
     * 将指定的字节数组提交给http响应对象并写入输出流
     *
     * @param b 需要被写入的字节数组
     * @throws IOException 抛出IO读写异常
     */
    @Override
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    /**
     * 将指定的字节数组提交给http响应对象并写入输出流
     *
     * @param b   需要被写入的字节数组
     * @param off 字节数组开始索引
     * @param len 字节数组的长度
     * @throws IOException 抛出IO读写异常
     */
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (closed) {
            throw new IOException("输入流已被关闭！");
        }

        int actual = len;   //实际写入长度
        if (length > 0 && (count + len) >= length) {
            //若超过报文主体的总长度，则截掉字节数组
            actual = length - count;
        }
        response.write(b, off, actual);
        count += actual;
        if (actual < len) {
            throw new IOException("不能写入比报文主体更多的字节数！");
        }
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {

    }

    public boolean isCommit() {
        return commit;
    }

    public void setCommit(boolean commit) {
        this.commit = commit;
    }

    boolean isClosed() {
        return closed;
    }

    /**
     * 将写入此流的字节数重置为零.
     */
    void reset() {
        count = 0;
    }
}
