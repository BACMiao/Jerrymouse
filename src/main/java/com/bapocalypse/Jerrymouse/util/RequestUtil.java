package com.bapocalypse.Jerrymouse.util;

import javax.servlet.http.Cookie;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;

/**
 * @package: com.bapocalypse.Jerrymouse.util
 * @Author: 陈淼
 * @Date: 2017/1/2
 * @Description: 请求的工具类，final类表示功能是完善的，且不能再被继承
 */
public final class RequestUtil {

    /**
     * @param map
     * @param data
     * @param encoding
     * @throws UnsupportedEncodingException
     */
    public static void parseParameters(Map map, String data, String encoding)
            throws UnsupportedEncodingException {
        if (data != null && data.length() > 0) {
            int length = data.length();
            byte[] bytes = data.getBytes();
            parseParameters(map, bytes, encoding);
        }
    }

    public static void parseParameters(Map map, byte[] bytes, String encoding) {

    }

    /**
     * 解析Cookie，例如 Cookie: userName=cm; password=pwd;
     *
     * @param cookieValue 请求首部信息中的键值(userName=cm; password=pwd;)
     * @return Cookie类型的数组
     */
    public static Cookie[] parseCookieHeader(String cookieValue) {
        //Cookie值为空
        if (cookieValue == null || cookieValue.length() < 1) {
            return new Cookie[0];
        }

        ArrayList<Cookie> cookies = new ArrayList<>();
        while (cookieValue.length() > 0) {
            int semicolon = cookieValue.indexOf(';');
            if (semicolon == 0) {
                break;
            }
            //获取其中的一个cookie eg：userName=cm;
            String token = cookieValue.substring(0, semicolon);
            if (semicolon < cookieValue.length()) {
                //余下的cookie eg: password=pwd;
                cookieValue = cookieValue.substring(semicolon + 1);
            } else {
                cookieValue = "";
            }
            int equals = token.indexOf('=');
            if (equals > 0) {
                String name = token.substring(0, equals).trim();
                String value = token.substring(equals + 1).trim();
                cookies.add(new Cookie(name, value));
            }
        }
        return cookies.toArray(new Cookie[cookies.size()]);
    }
}
