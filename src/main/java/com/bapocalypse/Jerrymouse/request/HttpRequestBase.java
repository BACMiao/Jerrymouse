package com.bapocalypse.Jerrymouse.request;

import com.bapocalypse.Jerrymouse.util.Enumerator;
import com.bapocalypse.Jerrymouse.util.ParameterMap;
import com.bapocalypse.Jerrymouse.util.RequestUtil;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.security.Principal;
import java.util.*;

/**
 * @package: com.bapocalypse.Jerrymouse.request
 * @Author: 陈淼
 * @Date: 2016/12/21
 * @Description: HTTP请求的类
 */
public class HttpRequestBase implements HttpServletRequest, ServletRequest {
    private HashMap<String, ArrayList<String>> headers = new HashMap<>(); //HTTP请求的请求头
    private ArrayList<Cookie> cookies = new ArrayList<>();                //HTTP的Cookie信息
    private ParameterMap<String, String[]> parameters = null;             //HTTP请求参数信息
    private static ArrayList<String> empty = new ArrayList<>();           //空集合，代表头部信息为空
    private BufferedReader reader = null;
    private ServletInputStream stream = null;
    private InputStream inputStream;           //输入流，用以保存请求输入流

    private String queryString;                //URI中的查询字符串
    private String requestedSessionId;         //URI中的会话标识符
    private boolean requestedSessionURL;       //查询字符串中是否包含会话标识符
    private String method;                     //请求行中的方法
    private String protocol;                   //请求行中的协议
    private String requestURI;                 //请求行中的URI
    private String contentType;                //请求报文主体的类型
    private int contentLength;                 //请求报文主体的长度
    private boolean requestedSessionCookie;
    private boolean parsed = false;            //该请求的参数是否已经被解析了
    private String contextPath = "";           //该请求的上下文路径

    /**
     * 将首部信息存入到request中的HashMap里面
     *
     * @param name  请求首部的名字
     * @param value 请求首部的值
     */
    public void addHeader(String name, String value) {
        name = name.toLowerCase();
        //同步锁，保证同一个时间内只有一个线程能改写headers
        synchronized (headers) {
            //取出键名为name的键值列表
            ArrayList<String> values = headers.get(name);
            if (values == null) {
                values = new ArrayList<>();
                headers.put(name, values);
                values.add(value);
            } else {
                values.add(value);
            }
        }
    }

    /**
     * 将首部信息中的cookie存入到request中的列表中
     *
     * @param cookie 首部的cookie
     */
    public void addCookie(Cookie cookie) {
        synchronized (cookies) {
            cookies.add(cookie);
        }
    }

    /**
     * 获取request中的所有Cookie值
     *
     * @return request请求头中的Cookie列表
     */
    @Override
    public Cookie[] getCookies() {
        synchronized (cookies) {
            if (cookies.size() < 1) {
                return null;
            } else {
                Cookie[] results = new Cookie[cookies.size()];
                return cookies.toArray(results);
            }
        }
    }

    /**
     * 如果参数尚未处理，则解析这个请求的参数。
     * 如果参数存在与查询字符串或HTTP请求体中，该方法会对这两者进行检查，
     * 解析完成后，参数会存储到对象变量parameters中。
     */
    private void parseParameters() {
        if (parsed) {
            //获取的参数已经被解析
            return;
        }
        ParameterMap<String, String[]> results = parameters;
        if (results == null) {
            results = new ParameterMap<>();
        }
        //打开parameterMap对象的锁，使其可操作
        results.setLocked(false);
        //检查字符串编码，若encoding为null，则使用默认编码
        String encoding = getCharacterEncoding();
        if (encoding == null) {
            encoding = "ISO-8859-1";
        }
        //获得查询字符串
        String queryString = getQueryString();
        try {
            //对查询字符串进行解析并存入指定map中
            RequestUtil.parseParameters(results, queryString, encoding);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //获得报文主体的实体类型
        String contentType = getContentType();
        if (contentType == null) {
            contentType = "";
        }
        //eg Content-Type:text/plain; charset=UTF-8。这里的contentType的值为text/plain; charset=UTF-8
        //eg Content-Type:text/plain。这里的contentType的值为text/plain
        int semicolon = contentType.indexOf(";");
        if (semicolon >= 0) {
            contentType = contentType.substring(0, semicolon).trim();
        } else {
            contentType = contentType.trim();
        }
        //使用POST方式提交请求，所以请求体中会包含参数
        if ("POST".equals(getMethod()) && getContentLength() > 0
                && "application/x-www-from-urlencoded".equals(contentType)) {
            try {
                int max = getContentLength(); //请求报文主体的长度
                int len = 0;                  //已经读取的长度
                //构建缓冲区
                byte[] buffer = new byte[getContentLength()];
                ServletInputStream servletInputStream = getInputStream();
                while (len < max) {
                    //将输入流的字节读入缓冲区，并返回读入的字节数
                    int next = servletInputStream.read(buffer, len, max - len);
                    if (next < 0) {
                        break;
                    }
                    len += next;
                }
                //关闭输入流
                servletInputStream.close();
                if (len < max) {
                    throw new RuntimeException("内容长度不匹配！");
                }
                RequestUtil.parseParameters(results, buffer, encoding);
            } catch (IOException e) {
                throw new RuntimeException("报文主体内容读取错误");
            }
        }
        //锁定ParameterMap对象，使其不能对其更改
        results.setLocked(true);
        //设置查询字符串已经被解析
        parsed = true;
        parameters = results;
    }

    @Override
    public String getHeader(String name) {
        name = name.toLowerCase();
        synchronized (headers) {
            ArrayList<String> values = headers.get(name);
            if (values != null) {
                return values.get(0);
            } else {
                return null;
            }
        }
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        name = name.toLowerCase();
        synchronized (headers) {
            ArrayList<String> values = headers.get(name);
            if (values != null) {
                return new Enumerator<>(values);
            } else {
                return new Enumerator<>(empty);
            }
        }
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        synchronized (headers) {
            return new Enumerator<>(headers);
        }
    }

    @Override
    public long getDateHeader(String s) {
        return 0;
    }

    @Override
    public String getAuthType() {
        return null;
    }

    @Override
    public int getIntHeader(String s) {
        return 0;
    }

    @Override
    public String getPathInfo() {
        return null;
    }

    @Override
    public String getPathTranslated() {
        return null;
    }

    @Override
    public String getRemoteUser() {
        return null;
    }

    @Override
    public boolean isUserInRole(String s) {
        return false;
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public String getRequestedSessionId() {
        return requestedSessionId;
    }

    @Override
    public StringBuffer getRequestURL() {
        return null;
    }

    @Override
    public String getServletPath() {
        return null;
    }

    @Override
    public HttpSession getSession(boolean b) {
        return null;
    }

    @Override
    public HttpSession getSession() {
        return null;
    }

    @Override
    public String changeSessionId() {
        return null;
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }

    @Override
    public boolean authenticate(HttpServletResponse httpServletResponse) throws IOException, ServletException {
        return false;
    }

    @Override
    public void login(String s, String s1) throws ServletException {

    }

    @Override
    public void logout() throws ServletException {

    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        return null;
    }

    @Override
    public Part getPart(String s) throws IOException, ServletException {
        return null;
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> aClass) throws IOException, ServletException {
        return null;
    }

    @Override
    public Object getAttribute(String s) {
        return null;
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return null;
    }

    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public void setCharacterEncoding(String s) throws UnsupportedEncodingException {

    }

    @Override
    public long getContentLengthLong() {
        return 0;
    }

    /**
     * 获得一个ServletInputStream，用以将输入流的字节存储到缓冲区
     *
     * @return 返回一个特定的ServletInputStream
     * @throws IOException 抛出IO异常
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (reader != null) {
            throw new IllegalStateException("getInputStream方法已经被调用！");
        }
        //如果ServletInputStream实例对象为空，则初始化
        if (stream == null) {
            stream = createInputStream();
        }
        return stream;
    }

    /**
     * 创建和返回一个用来去读取与request相关的报文信息Servlet的输入流
     *
     * @return 返回请求的流
     * @throws IOException 抛出IO读写异常
     */
    private ServletInputStream createInputStream() throws IOException {
        return (new RequestStream(this));
    }

    /**
     * 根据键来获取参数所对应的第一个值
     *
     * @param name 需要获取信息的键
     * @return 返回键所对应的第一个值
     */
    @Override
    public String getParameter(String name) {
        parseParameters();
        String[] values = parameters.get(name);
        if (values != null) {
            return values[0];
        } else {
            return null;
        }
    }

    /**
     * 获取所有查询字符串被解析后的键
     *
     * @return 返回一个枚举器
     */
    @Override
    public Enumeration<String> getParameterNames() {
        parseParameters();
        return new Enumerator<>(parameters);
    }

    /**
     * 根据键来获取参数所对应的所有值
     *
     * @param name 需要获取信息的键
     * @return 返回键所对应的所有值
     */
    @Override
    public String[] getParameterValues(String name) {
        parseParameters();
        String[] values = parameters.get(name);
        if (values != null) {
            return values;
        } else {
            return null;
        }
    }

    /**
     * 获取请求中查询字符串被解析后所存入的map
     *
     * @return 返回存有参数信息的map
     */
    @Override
    public Map<String, String[]> getParameterMap() {
        parseParameters();
        return this.parameters;
    }

    @Override
    public String getScheme() {
        return null;
    }

    @Override
    public String getServerName() {
        return null;
    }

    @Override
    public int getServerPort() {
        return 0;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return null;
    }

    @Override
    public String getRemoteAddr() {
        return null;
    }

    @Override
    public String getRemoteHost() {
        return null;
    }

    @Override
    public void setAttribute(String s, Object o) {

    }

    @Override
    public void removeAttribute(String s) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return null;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String s) {
        return null;
    }

    @Override
    public String getRealPath(String s) {
        return null;
    }

    @Override
    public int getRemotePort() {
        return 0;
    }

    @Override
    public String getLocalName() {
        return null;
    }

    @Override
    public String getLocalAddr() {
        return null;
    }

    @Override
    public int getLocalPort() {
        return 0;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        return null;
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        return null;
    }

    @Override
    public boolean isAsyncStarted() {
        return false;
    }

    @Override
    public boolean isAsyncSupported() {
        return false;
    }

    @Override
    public AsyncContext getAsyncContext() {
        return null;
    }

    @Override
    public DispatcherType getDispatcherType() {
        return null;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    @Override
    public String getQueryString() {
        return queryString;
    }

    public void setRequestedSessionId(String requestedSessionId) {
        this.requestedSessionId = requestedSessionId;
    }

    public void setRequestedSessionURL(boolean requestedSessionURL) {
        this.requestedSessionURL = requestedSessionURL;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public String getMethod() {
        return method;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @Override
    public String getProtocol() {
        return protocol;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    @Override
    public String getRequestURI() {
        return requestURI;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    @Override
    public int getContentLength() {
        return contentLength;
    }

    public void setStream(InputStream inputstream) {
        this.inputStream = inputstream;
    }

    public InputStream getStream(){
        return inputStream;
    }

    public boolean isRequestedSessionCookie() {
        return requestedSessionCookie;
    }

    public void setRequestedSessionCookie(boolean requestedSessionCookie) {
        this.requestedSessionCookie = requestedSessionCookie;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    @Override
    public String getContextPath() {
        return contextPath;
    }

    public void setInputStream(ServletInputStream stream) {
        this.stream = stream;
    }
}
