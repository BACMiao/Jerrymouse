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
     * 解析查询字符串，并将解析后的键值对存入指定map中
     *
     * @param map      参数的键值对
     * @param data     还未解析查询查数
     * @param encoding 指定编码
     * @throws UnsupportedEncodingException 抛出未支持编码异常
     */
    public static void parseParameters(Map<String, String[]> map, String data, String encoding)
            throws UnsupportedEncodingException {
        if (data != null && data.length() > 0) {
            byte[] bytes = data.getBytes();
            parseParameters(map, bytes, encoding);
        }
    }

    /**
     * 解析查询字符串，并将解析后的键值对存入指定map中
     *
     * @param map      参数的键值对
     * @param data     还未解析查询查数
     * @param encoding 指定编码
     * @throws UnsupportedEncodingException 抛出未支持编码异常
     */
    public static void parseParameters(Map<String, String[]> map, byte[] data, String encoding)
            throws UnsupportedEncodingException {
        if (data != null && data.length > 0) {
            int ix = 0;    //当前读取索引
            int ox = 0;    //读取输入索引（会在遇见指定字符后重置）
            String key = null;
            String value;
            while (ix < data.length) {
                //假设data中的初始数据为name=cm&password=123
                byte c = data[ix++];
                switch (c) {
                    case '&':
                        //第一次ox的值为2，即需要解析的字符数为2个，即cm
                        value = new String(data, 0, ox, encoding);
                        if (key != null) {
                            //将键值对存入map中
                            putMapEntry(map, key, value);
                            key = null;
                        }
                        ox = 0;
                        break;
                    case '=':
                        //第一次ox的值为4，即需要解析的字符数为4个，即name
                        key = new String(data, 0, ox, encoding);
                        ox = 0;
                        break;
                    case '+':
                        data[ox++] = ' ';
                        break;
                    case '%':
                        data[ox++] = (byte) (convertHexDigit(data[ix++]) << 4
                                + convertHexDigit(data[ix++]));
                        break;
                    default:
                        data[ox++] = c;
                }
            }
            //最后的一个查询键值对
            if (key != null) {
                value = new String(data, 0, ox, encoding);
                putMapEntry(map, key, value);
            }
        }
    }

    /**
     * 将键值对放入到map中，不覆盖旧值，而是将旧值和新值合并后赋予新的数组
     *
     * @param map   将要存放键值对的map
     * @param name  需要被存放的键
     * @param value 需要被存放的值
     */
    private static void putMapEntry(Map<String, String[]> map, String name, String value) {
        String[] newValues;          //新的值
        String[] oldValues = map.get(name); //旧的值
        if (oldValues == null) {
            //若是新添加的键值对，则将传入的值赋予数组
            newValues = new String[1];
            newValues[0] = value;
        } else {
            //若是map中存在旧值，则将旧值赋予新数组，并添加新值
            newValues = new String[oldValues.length + 1];
            System.arraycopy(oldValues, 0, newValues, 0,
                    oldValues.length);
            newValues[oldValues.length] = value;
        }
        map.put(name, newValues);
    }

    /**
     * 将一个字节字符值转变为16进制的数值（eg 4->4, B->11, c->12）
     *
     * @param b 需要转变的字节字符
     * @return 16进制数值
     */
    private static byte convertHexDigit(byte b) {
        if (b >= '0' && b <= '9') {
            return (byte) (b - '0');
        } else if (b >= 'a' && b <= 'f') {
            return (byte) (b - 'a' + 10);
        } else if (b >= 'A' && b <= 'F') {
            return (byte) (b - 'A' + 10);
        } else {
            return 0;
        }
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
        //将cookies转换为为数组并存入到new Cookie[cookies.size()]
        return cookies.toArray(new Cookie[cookies.size()]);
    }
}
