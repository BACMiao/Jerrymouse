package com.bapocalypse.Jerrymouse.response;

import com.bapocalypse.Jerrymouse.request.HttpRequestBase;
import com.bapocalypse.Jerrymouse.util.Constants;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Collection;
import java.util.Locale;

/**
 * @package: com.bapocalypse.Jerrymouse.response
 * @Author: 陈淼
 * @Date: 2016/12/30
 * @Description: HTTP响应的类
 */
public class HttpResponseBase implements HttpServletResponse, ServletResponse {
    private static final int BUFFER_SIZE = 1024;
    private OutputStream outputStream;               //输出流
    private HttpRequestBase request;
    private PrintWriter writer;

    private byte[] buffer = new byte[BUFFER_SIZE]; //缓冲区
    private int bufferCount = 0;                   //当前缓冲区中的字节数量
    private int contentCount = 0;                  //写入此response响应的实际字节数（不断递增）
    private String encoding = null;                //该响应相关的字符编码
    private String contentType = null;             //响应报文主体的类型

    /**
     * sendStaticResource()方法用于发送一个静态资源到浏览器
     */
    public void sendStaticResource() throws IOException {
        byte[] bytes = new byte[BUFFER_SIZE];
        FileInputStream fis = null;
        try {
            //传入父路径和子路径
            File file = new File(Constants.WEB_ROOT, request.getRequestURI());
            if (file.exists()) {
                fis = new FileInputStream(file);
                //读取文件信息到byte数组中
                int ch = fis.read(bytes, 0, BUFFER_SIZE);
                while (ch != -1) {
                    outputStream.write(bytes, 0, ch);
                    ch = fis.read(bytes, 0, BUFFER_SIZE);
                }
            } else {
                String errorMessage = "HTTP/1.1 404 File Not Found\r\n" +
                        "Content-Type: text/html\r\n" +
                        "Content-Length: 23\r\n" +
                        "\r\n" +
                        "<h1>File Not Found</h1>";
                outputStream.write(errorMessage.getBytes());
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }

    /**
     * 将特定的字节写入缓冲区中
     *
     * @param b 需要写入缓冲区的字节
     * @throws IOException 抛出IO读写异常
     */
    public void write(int b) throws IOException {
        //说明当前缓冲区已经满了，刷新缓冲区
        if (bufferCount >= buffer.length) {
            flushBuffer();
        }
        buffer[bufferCount++] = (byte) b;
        contentCount++;
    }

    /**
     * 将特定的字节数组写入到缓冲区中
     *
     * @param b 需要写入缓冲区的字节数组
     * @throws IOException 抛出IO读写异常
     */
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    /**
     * 将特定的字节数组写入到缓冲区中，可能字节数组的大小会大于缓冲区，
     * 所以需要边刷新缓冲区边写入
     *
     * @param b   需要写入缓冲区的字节数组
     * @param off 字节数组的开始索引
     * @param len 字节数组的长度
     * @throws IOException 抛出IO读写异常
     */
    public void write(byte[] b, int off, int len) throws IOException {
        if (len == 0) {
            return;
        }
        //如果当前缓冲区剩余的空间，直接将特定数组写入缓冲区
        if (len <= buffer.length - bufferCount) {
            System.arraycopy(b, off, buffer, bufferCount, len);
            bufferCount += len;
            contentCount += len;
            return;
        }
        //说明当前缓冲区剩余空间不够，刷新缓冲区，并开始写入到完整的缓冲区块
        flushBuffer();
        int iterations = len / buffer.length;            //需要写满几块缓冲区
        int leftoverStart = iterations * buffer.length;  //字节数组超出且未满一块缓冲区的字节的开始索引
        int leftoverLen = len - leftoverStart;           //字节数组超出且未满一块缓冲区的字节的长度
        for (int i = 0; i < iterations; i++) {
            //递归，写满整块缓冲区
            write(b, off + (i * buffer.length), buffer.length);
        }
        //将剩下的字节写入缓冲区
        if (leftoverLen > 0) {
            write(b, off + leftoverStart, leftoverLen);
        }
    }

    /**
     * 调用这个方法为输出发送首部信息和响应，否则页面将没有显示信息
     */
    public void finishResponse() {
        if (writer != null) {
            writer.flush();
            writer.close();
        }
    }

    @Override
    public void addCookie(Cookie cookie) {

    }

    @Override
    public boolean containsHeader(String s) {
        return false;
    }

    @Override
    public String encodeURL(String s) {
        return null;
    }

    @Override
    public String encodeRedirectURL(String s) {
        return null;
    }

    @Override
    public String encodeUrl(String s) {
        return null;
    }

    @Override
    public String encodeRedirectUrl(String s) {
        return null;
    }

    @Override
    public void sendError(int i, String s) throws IOException {

    }

    @Override
    public void sendError(int i) throws IOException {
    }

    @Override
    public void sendRedirect(String s) throws IOException {

    }

    @Override
    public void setDateHeader(String s, long l) {

    }

    @Override
    public void addDateHeader(String s, long l) {

    }

    @Override
    public void setHeader(String name, String value) {

    }

    @Override
    public void addHeader(String s, String s1) {

    }

    @Override
    public void setIntHeader(String s, int i) {

    }

    @Override
    public void addIntHeader(String s, int i) {

    }

    @Override
    public void setStatus(int i) {

    }

    @Override
    public void setStatus(int i, String s) {

    }

    @Override
    public int getStatus() {
        return 0;
    }

    @Override
    public String getHeader(String s) {
        return null;
    }

    @Override
    public Collection<String> getHeaders(String s) {
        return null;
    }

    @Override
    public Collection<String> getHeaderNames() {
        return null;
    }

    @Override
    public String getCharacterEncoding() {
        if (encoding == null) {
            return "ISO-8859-1";
        } else {
            return encoding;
        }
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        ResponseStream newStream = new ResponseStream(this);
        newStream.setCommit(false);
        //创建使用指定字符集的OutputStreamWriter，使传入的字符转换为指定字符集的字节数组
        OutputStreamWriter osr = new OutputStreamWriter(newStream, getCharacterEncoding());
        writer = new ResponseWriter(osr);
        return writer;
    }

    @Override
    public void setCharacterEncoding(String s) {

    }

    @Override
    public void setContentLength(int i) {

    }

    @Override
    public void setContentLengthLong(long l) {

    }

    @Override
    public void setContentType(String s) {
        this.contentType = s;
    }

    @Override
    public void setBufferSize(int i) {

    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    /**
     * 刷新缓冲区，将缓冲区中的字节全部写入到输出流里面
     *
     * @throws IOException 抛出IO读写异常
     */
    @Override
    public void flushBuffer() throws IOException {
        //如果已经有字节存在于缓冲区
        if (bufferCount > 0) {
            try {
                //将缓冲区中的实际字节写入到输出流里面
                outputStream.write(buffer, 0, bufferCount);
            } finally {
                //缓冲区中的字节全部写入到输出流后，当前的缓冲区字节数量为0
                bufferCount = 0;
            }
        }
    }

    @Override
    public void resetBuffer() {

    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public void setLocale(Locale locale) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }

    public void setRequest(HttpRequestBase request) {
        this.request = request;
    }

    public void setStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public OutputStream getStream() {
        return outputStream;
    }
}
