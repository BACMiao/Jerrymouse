package com.bapocalypse.Jerrymouse.connector.http;

/**
 * @package: com.bapocalypse.Jerrymouse.connector.http
 * @Author: 陈淼
 * @Date: 2016/12/22
 * @Description:
 */
public final class HttpRequestLine {
    private static final int INITIAL_METHOD_SIZE = 8;   //初始方法的大小
    private static final int INITIAL_URI_SIZE = 64;     //初始uri的大小
    private static final int INITIAL_PROTOCOL_SIZE = 8; //初始协议的大小
    public static final int MAX_METHOD_SIZE = 1024;     //最大方法的大小
    public static final int MAX_URI_SIZE = 32768;       //最大uri的大小
    public static final int MAX_PROTOCOL_SIZE = 1024;   //最大协议的大小

    public char[] method;
    public int methodEnd;
    public char[] uri;
    public int uriEnd;
    public char[] protocol;
    public int protocolEnd;

    public HttpRequestLine() {
        this(new char[INITIAL_METHOD_SIZE], 0, new char[INITIAL_URI_SIZE], 0,
                new char[INITIAL_PROTOCOL_SIZE], 0);
    }


    public HttpRequestLine(char[] method, int methodEnd,
                           char[] uri, int uriEnd,
                           char[] protocol, int protocolEnd) {
        this.method = method;
        this.methodEnd = methodEnd;
        this.uri = uri;
        this.uriEnd = uriEnd;
        this.protocol = protocol;
        this.protocolEnd = protocolEnd;
    }

    //判断
    public int index(String str){
        return 0;
    }
}
