package com.bapocalypse.Jerrymouse.util;

import java.io.UnsupportedEncodingException;
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
}
