package com.bapocalypse.Jerrymouse.connector.http;

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
    private int num;
    private byte[] buffer;

    public SocketInputStream(InputStream inputStream, int bufferSize) {
        this.inputStream = inputStream;
        buffer = new byte[bufferSize];
    }

    /**
     * 返回HTTP的第一行的内容，即包含URI、请求方法和HTTP版本信息
     * @param httpRequestLine
     * @return
     */
    public HttpRequestLine readRequestLine(HttpRequestLine httpRequestLine) {
        return null;
    }

    /**
     * 读取所有请求头信息
     * @return
     */
    public HttpHeader readHeader() {
        return null;
    }


    @Override
    public int read() throws IOException {
        return 0;
    }
}
