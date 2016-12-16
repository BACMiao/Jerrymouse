package com.bapocalypse.Jerrymouse;

import java.io.IOException;
import java.io.InputStream;

/**
 * @package: com.bapocalypse.Jerrymouse
 * @Author: 陈淼
 * @Date: 2016/12/16
 * @Description: 表示一个HTTP请求
 */
public class Request {
    private InputStream inputStream;
    private String uri;

    public Request(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * 用于解析HTTP请求中的原始数据
     */
    public void parse() {
        StringBuilder request = new StringBuilder(2048);
        int i;
        byte[] buffer = new byte[2048];
        try {
            //inputStream.read(byte[] buffer) 从输入流中读取一定数量的字节，
            // 并将其存储在缓冲区数组buffer中，返回读入缓冲区的总字节数
            i = inputStream.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            i = -1;
        }
        for (int j = 0; j < i; j++) {
            request.append((char) buffer[j]);
        }
        System.out.println(request.toString());
        uri = parseUri(request.toString());
    }

    /**
     * 将URI存储在变量uri中
     */
    private String parseUri(String requestString) {
        int index1, index2;
        //第一个空格的索引
        index1 = requestString.indexOf(' ');
        if (index1 != -1) {
            //第二个空格的索引
            index2 = requestString.indexOf(' ', index1 + 1);
            if (index2 > index1) {
                //返回第一个空格和第二个空格的字段，（GET /index.html HTTP/1.1）即/index.html
                return requestString.substring(index1 + 1, index2);
            }
        }
        return null;
    }

    public String getUri() {
        return uri;
    }
}
