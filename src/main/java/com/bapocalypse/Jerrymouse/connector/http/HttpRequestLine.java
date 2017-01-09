package com.bapocalypse.Jerrymouse.connector.http;

/**
 * @package: com.bapocalypse.Jerrymouse.connector.http
 * @Author: 陈淼
 * @Date: 2016/12/22
 * @Description: HTTP请求行的判定，用于判断指定字符串在请求行中是否存在
 */
public final class HttpRequestLine {
    private static final int INITIAL_METHOD_SIZE = 8;   //初始方法的大小
    private static final int INITIAL_URI_SIZE = 64;     //初始uri的大小
    private static final int INITIAL_PROTOCOL_SIZE = 8; //初始协议的大小
    static final int MAX_METHOD_SIZE = 1024;     //最大方法的大小
    static final int MAX_URI_SIZE = 32768;       //最大uri的大小
    static final int MAX_PROTOCOL_SIZE = 1024;   //最大协议的大小

    char[] method;
    int methodEnd;     //方法数组的最后一个字符的索引值
    char[] uri;
    int uriEnd;        //URI数组的最后一个字符的索引值
    char[] protocol;
    int protocolEnd;   //协议数组的最后一个字符的索引值

    public HttpRequestLine() {
        this(new char[INITIAL_METHOD_SIZE], 0,
                new char[INITIAL_URI_SIZE], 0,
                new char[INITIAL_PROTOCOL_SIZE], 0);
    }


    private HttpRequestLine(char[] method, int methodEnd,
                            char[] uri, int uriEnd,
                            char[] protocol, int protocolEnd) {
        this.method = method;
        this.methodEnd = methodEnd;
        this.uri = uri;
        this.uriEnd = uriEnd;
        this.protocol = protocol;
        this.protocolEnd = protocolEnd;
    }

    /**
     * 释放所有对象的引用，并初始化实例变量，为重复使用做准备
     */
    void recycle() {
        methodEnd = 0;
        uriEnd = 0;
        protocolEnd = 0;
    }

    /**
     * 判断URI是否包含指定字符串
     *
     * @param str 指定字符串
     * @return 指定字符串在URI中的出现的首字符的索引值
     */
    public int uriIndexOf(String str) {
        return uriIndexOf(str.toCharArray(), str.length());
    }

    /**
     * 判断URI中是否包含指定字符数组
     *
     * @param buf 指定字符串数组
     * @param end 数组的长度
     * @return 指定字符数组在URI中的出现的首字符的索引值
     */
    private int uriIndexOf(char[] buf, int end) {
        char firstChar = buf[0];
        int pos = 0;
        while (pos < uriEnd) {
            //首字符在URI串中第一次出现的索引值
            pos = uriIndexOf(firstChar, pos);
            if (pos == -1) {
                return -1;
            } else if ((uriEnd - pos) < end) {
                //从找到第一个匹配的字符的位置到末尾的长度小于指定数组长度
                return -1;
            }
            //判断是否包含指定数组
            for (int i = 0; i < end; i++) {
                if (uri[i + pos] != buf[i])
                    break;
                //说明指定字符数组在URI中找到了，返回首字符的索引
                if (i == (end - 1))
                    return pos;
            }
            pos++;
        }
        return -1;
    }

    /**
     * 返回指定字符在URI的中第一次出现的位置的索引值
     *
     * @param c     指定字符
     * @param start 开始的索引值
     * @return 指定字符的索引
     */
    private int uriIndexOf(char c, int start) {
        for (int i = start; i < uriEnd; i++) {
            if (uri[i] == c) {
                return i;
            }
        }
        return -1;
    }
}
