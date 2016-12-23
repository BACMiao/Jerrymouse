package com.bapocalypse.Jerrymouse.connector.http;

/**
 * @package: com.bapocalypse.Jerrymouse.connector.http
 * @Author: 陈淼
 * @Date: 2016/12/22
 * @Description:
 */
final class HttpRequestLine {
    private static final int INITIAL_METHOD_SIZE = 8;
    private static final int INITIAL_URI_SIZE = 64;
    private static final int INITIAL_PROTOCOL_SIZE = 8;
    public static final int MAX_METHOD_SIZE = 1024;
    public static final int MAX_URI_SIZE = 32768;
    public static final int MAX_PROTOCOL_SIZE = 1024;

    public char[] method;
    private char[] uri;
    private char[] protocol;

}
