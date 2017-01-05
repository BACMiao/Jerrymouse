package com.bapocalypse.Jerrymouse.response;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * @package: com.bapocalypse.Jerrymouse.response
 * @Author: 陈淼
 * @Date: 2017/1/4
 * @Description: 用来向客户端发送信息，使用ResponseWriter来构建writer实例，
 * 当一个print或者println方法被调用的时候，会自动刷新缓冲
 */
public class ResponseWriter extends PrintWriter {
    public ResponseWriter(OutputStreamWriter outputStreamWriter) {
        super(outputStreamWriter);
    }

    @Override
    public void print(boolean b) {
        super.print(b);
        super.flush();
    }

    @Override
    public void print(char c) {
        super.print(c);
        super.flush();
    }

    @Override
    public void print(int i) {
        super.print(i);
        super.flush();
    }

    @Override
    public void print(long l) {
        super.print(l);
        super.flush();
    }

    @Override
    public void print(float f) {
        super.print(f);
        super.flush();
    }

    @Override
    public void print(double d) {
        super.print(d);
        super.flush();
    }

    @Override
    public void print(char[] s) {
        super.print(s);
        super.flush();
    }

    @Override
    public void print(String s) {
        super.print(s);
        super.flush();
    }

    @Override
    public void print(Object obj) {
        super.print(obj);
        super.flush();
    }

    @Override
    public void println() {
        super.println();
        super.flush();
    }

    @Override
    public void println(boolean x) {
        super.println(x);
        super.flush();
    }

    @Override
    public void println(char x) {
        super.println(x);
        super.flush();
    }

    @Override
    public void println(int x) {
        super.println(x);
        super.flush();
    }

    @Override
    public void println(long x) {
        super.println(x);
        super.flush();
    }

    @Override
    public void println(float x) {
        super.println(x);
        super.flush();
    }

    @Override
    public void println(double x) {
        super.println(x);
        super.flush();
    }

    @Override
    public void println(char[] x) {
        super.println(x);
        super.flush();
    }

    @Override
    public void println(String x) {
        super.println(x);
        super.flush();
    }

    @Override
    public void println(Object x) {
        super.println(x);
        super.flush();
    }

    @Override
    public void write(int c) {
        super.write(c);
        super.flush();
    }

    @Override
    public void write(char[] buf, int off, int len) {
        super.write(buf, off, len);
        super.flush();
    }

    @Override
    public void write(char[] buf) {
        super.write(buf);
        super.flush();
    }

    @Override
    public void write(String s, int off, int len) {
        super.write(s, off, len);
        super.flush();
    }

    @Override
    public void write(String s) {
        super.write(s);
        super.flush();
    }
}
