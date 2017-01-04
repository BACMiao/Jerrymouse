package com.bapocalypse.Jerrymouse.connector.http;

/**
 * @package: com.bapocalypse.Jerrymouse.connector.http
 * @Author: 陈淼
 * @Date: 2016/12/30
 * @Description: HTTP请求首部字段分解和判定，用于判断指定字符串在请求首部字段中是否存在
 * 和两个HttpHeader对象是否相等。
 */
public final class HttpHeader {
    private static final int INITIAL_NAME_SIZE = 32;      //初始键名的大小
    private static final int INITIAL_VALUE_SIZE = 64;     //初始键值的大小
    static final int MAX_NAME_SIZE = 128;                 //最大键名的大小
    static final int MAX_VALUE_SIZE = 4096;               //最大键值的大小

    public char[] name;
    public int nameEnd;    //键名数组的最后一个字符的索引值
    public char[] value;
    public int valueEnd;   //键值数组的最后一个字符的索引值
    protected int hashCode = 0;

    public HttpHeader() {
        this(new char[INITIAL_NAME_SIZE], 0,
                new char[INITIAL_VALUE_SIZE], 0);
    }

    private HttpHeader(char[] name, int nameEnd, char[] value, int valueEnd) {
        this.name = name;
        this.nameEnd = nameEnd;
        this.value = value;
        this.valueEnd = valueEnd;
    }

    public HttpHeader(String name, String value) {
        this.name = name.toLowerCase().toCharArray();
        this.nameEnd = name.length();
        this.value = value.toCharArray();
        this.valueEnd = value.length();
    }

    /**
     * 释放所有对象的引用，并初始化实例变量，为重复使用做准备
     */
    public void recycle() {
        nameEnd = 0;
        valueEnd = 0;
        hashCode = 0;
    }

    /**
     * 判断HTTP请求头部字段的键名是否等于给定的char数组
     *
     * @param buf 给定的char数组
     * @return 若等于，返回真；不等于，返回假
     */
    public boolean nameEquals(char[] buf) {
        return nameEquals(buf, buf.length);
    }

    /**
     * 判断HTTP请求头部字段的键名是否等于给定的字符串
     *
     * @param str 给定的字符串
     * @return 若等于，返回真；不等于，返回假
     */
    public boolean nameEquals(String str) {
        return nameEquals(str.toCharArray(), str.length());
    }

    /**
     * 判断HTTP请求头部字段的键名是否等于给定的char数组，
     * 所有的字符必须已经转为小写
     *
     * @param buf 给定的char数组
     * @param end 给定的char数组的长度
     * @return 若等于，返回真；不等于，返回假
     */
    private boolean nameEquals(char[] buf, int end) {
        if (end != nameEnd) {
            return false;
        }
        for (int i = 0; i < end; i++) {
            if (buf[i] != name[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断HTTP请求头部字段的键值是否等于给定header中的头部信息。
     *
     * @param header 给定的请求头部信息
     * @return 若等于，返回真；不等于，返回假
     */
    public boolean nameEquals(HttpHeader header) {
        return nameEquals(header.name, header.nameEnd);
    }

    /**
     * 判断HTTP请求头部字段的键值是否等于给定的char数组
     *
     * @param buf 给定的char数组
     * @return 若等于，返回真；不等于，返回假
     */
    public boolean valueEquals(char[] buf) {
        return valueEquals(buf, buf.length);
    }

    /**
     * 判断HTTP请求头部字段的键值是否等于给定的字符串
     *
     * @param str 给定的字符串
     * @return 若等于，返回真；不等于，返回假
     */
    public boolean valueEquals(String str) {
        return valueEquals(str.toCharArray(), str.length());
    }

    /**
     * 判断HTTP请求头部字段的键值是否等于给定的char数组
     *
     * @param buf 给定的char数组
     * @param end 给定的char数组的长度
     * @return 若等于，返回真；不等于，返回假
     */
    private boolean valueEquals(char[] buf, int end) {
        if (end != valueEnd) {
            return false;
        }
        for (int i = 0; i < end; i++) {
            if (buf[i] != value[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断HTTP请求头部字段中的键名和键值等于给定的header，
     * 必须保证所有的键名已经转化为小写
     *
     * @param header 给定的头部信息
     * @return 若等于，返回真；不等于，返回假
     */
    public boolean headerEquals(HttpHeader header) {
        return nameEquals(header.name, header.nameEnd) &&
                valueEquals(header.value, header.valueEnd);
    }

    /**
     * 判断键值中是否包含指定字符串
     *
     * @param str 指定字符串
     * @return 若包含，返回真；否则，返回假
     */
    public boolean valueIncludes(String str) {
        return valueIncludes(str.toCharArray(), str.length());
    }

    /**
     * 判断键值中是否包含指定字符数组
     *
     * @param buf 指定字符数组
     * @return 若包含，返回真；否则，返回假
     */
    public boolean valueIncludes(char[] buf) {
        return valueIncludes(buf, buf.length);
    }

    /**
     * 判断键值中是否包含指定字符数组
     *
     * @param buf 给定的char数组
     * @param end 给定char数组的长度
     * @return 若包含，返回真；否则，返回假
     */
    private boolean valueIncludes(char[] buf, int end) {
        char firstChar = buf[0];
        int pos = 0;
        while (pos < valueEnd) {
            //首字符在键值中第一次出现的索引值
            pos = valueIndexOf(firstChar, pos);
            if (pos == -1) {
                return false;
            } else if ((valueEnd - pos) < end) {
                //从找到第一个匹配的字符的位置到末尾的长度小于指定数组长度
                return false;
            }
            //判断是否包含指定数组
            for (int i = 0; i < end; i++) {
                if (value[i + pos] != buf[i])
                    break;
                //说明指定字符数组在键值中找到了，返回首字符的索引
                if (i == (end - 1))
                    return true;
            }
            pos++;
        }
        return true;
    }

    /**
     * 返回指定字符在键值中第一次出现的位置的索引值
     *
     * @param c     指定字符
     * @param start 开始的索引值
     * @return 指定字符的索引
     */
    private int valueIndexOf(char c, int start) {
        for (int i = start; i < valueEnd; i++) {
            if (value[i] == c) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 重写hashCode方法，使得相等的对象hashCode也相等，
     * Java文档规定：重写equals()方法必须要重写hashCode()方法。
     *
     * @return 返回该对象的哈希码值。
     */
    @Override
    public int hashCode() {
        int h = hashCode;
        if (h == 0) {
            int off = 0;
            char[] val = name;
            int len = nameEnd;
            for (int i = 0; i < len; i++) {
                h = 31 * h + val[off++];
            }
            hashCode = h;
        }
        return h;
    }

    /**
     * 重写equals方法，使得具有相同键名的对象与此对象相等
     *
     * @param obj 要与之比较的引用对象
     * @return 如果此对象与 obj 参数相同，则返回 true；否则返回 false。
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof String) {
            return nameEquals(((String) obj).toLowerCase());
        } else if (obj instanceof HttpHeader) {
            return nameEquals((HttpHeader) obj);
        }
        return false;
    }
}
