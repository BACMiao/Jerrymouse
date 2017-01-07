package com.bapocalypse.Jerrymouse.connector.http;

import com.bapocalypse.Jerrymouse.processor.Processor;
import com.bapocalypse.Jerrymouse.processor.ServletProcessor;
import com.bapocalypse.Jerrymouse.processor.StaticResourceProcessor;
import com.bapocalypse.Jerrymouse.request.HttpRequestBase;
import com.bapocalypse.Jerrymouse.request.HttpRequestImpl;
import com.bapocalypse.Jerrymouse.response.HttpResponseBase;
import com.bapocalypse.Jerrymouse.response.HttpResponseImpl;
import com.bapocalypse.Jerrymouse.util.RequestUtil;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @package: com.bapocalypse.Jerrymouse.processor
 * @Author: 陈淼
 * @Date: 2016/12/21
 * @Description: 处理器类，读取套接字的输入流，解析Http请求，负责创建Request和Response对象
 */
public class HttpProcessor implements Runnable {
    private HttpRequestLine requestLine = new HttpRequestLine();
    private HttpRequestImpl request;
    private HttpResponseImpl response;
    private HttpConnector connector;
    private int id;
    private boolean stopped = false;    //钩子，表示HttpProcessor实例是否被连接器终止
    private boolean available = false;  //是否有新的可用套接字
    private Socket socket = null;
    private Object threadSync = new Object();
    private boolean keepAlive = false;  //该连接是否是持久连接

    public HttpProcessor(HttpConnector connector, int id) {
        this.request = connector.createRequest();
        this.response = connector.createResponse();
        this.connector = connector;
        this.id = id;
    }

    /**
     * 称HttpProcessor实例中run()方法运行时所在的线程为“处理器线程”。
     */
    @Override
    public void run() {
        //处理request请求，直到我们接收到一个关闭信号
        while (!stopped) {
            // TODO: 2017/1/7
            //获取套接字对象，执行到这儿会阻塞
            Socket socket = await();
            if (socket == null) {
                continue;
            }
            //对套接字对象进行处理
            process2(socket);
            //将当前的HttpProcessor实例压回栈中
            connector.recycle(this);
        }
        //告知threadStop()方法，我们已经成功关闭自己
        synchronized (threadSync) {
            threadSync.notifyAll();
        }
    }

    /**
     * await方法会阻塞处理器线程的控制流，直到它从HttpConnector中获取到新的Socket对象，
     * 也就是直到HttpConnector对象调用HttpProcessor实例的assign()方法前，都会一直阻塞。
     * 注意:await()和assign()方法并不是运行在同一个线程中。
     *
     * @return 返回接收的套接字对象
     */
    private synchronized Socket await() {
        while (!available) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //使用局部变量是因为可以在当前Socket对象（全局）处理完之前继续接收下一个Socket对象
        Socket socket = this.socket;
        available = false;
        //防止出现另一个Socket对象已经到达，而此时available的值还是true的情况
        notifyAll();
        return socket;
    }

    /**
     * assign()方法是在HttpConnector对象的run()方法中调用的，assign()方法通过
     * available的布尔变量和wait()以及notifyAll()方法进行沟通。
     *
     * @param socket 从HttpConnector中获取的新的Socket对象
     */
    synchronized void assign(Socket socket) {
        while (available) {
            try {
                //使当前线程进入等待状态
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.socket = socket;
        available = true;
        //唤醒当前处理器线程
        notifyAll();
    }

    /**
     * process()方法会执行以下3个操作：
     * 1、解析连接
     * 2、解析请求
     * 3、解析请求头
     *
     * @param socket
     */
    private void process2(Socket socket) {
        boolean ok = true;  //表示处理的过程中是否有错误发生
        boolean finishResponse = true; //表示是否应该调用Response中的finishResponse()方法
        SocketInputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new SocketInputStream(socket.getInputStream(), connector.getBufferSize());
        } catch (IOException e) {
            ok = false;
        }
        //不断地读取输入流，直到HttpProcessor实例终止或抛出异常或断开连接
        keepAlive = true;
        while (!stopped && ok && keepAlive) {
            finishResponse = true;
            request.setStream(inputStream);
        }
    }

    /**
     * 对每一个传入的HTTP请求，进行四步操作：
     * 1、创建一个HttpRequest对象
     * 2、创建一个HttpResponse对象
     * 3、解析HTTP请求的第一行内容和请求头信息，填充HttpRequest对象
     * 4、将HttpRequest对象和HttpResponse对象传递给servletResponse或者
     * StaticResourceProcessor对象的process()方法。
     *
     * @param socket 接收到的套接字对象
     */
//    public void process(Socket socket) {
//        OutputStream outputStream; //返回此套接字的输出流
//        SocketInputStream inputStream;   //返回此套接字的输入流
//        try {
//            outputStream = socket.getOutputStream();
//            inputStream = new SocketInputStream(socket.getInputStream(), 2048);
//            request = new HttpRequestBase(inputStream);
//            response = new HttpResponseBase(outputStream);
//
//            response.setRequest(request);
//            //调用HttpResponse类的setHeader()方法向客户端发送响应头信息
//            response.setHeader("Server", "Jerrymouse Servlet Container");
//            //解析请求行信息
//            parseRequest(inputStream, outputStream);
//            //解析请求首部字段信息
//            parseHeader(inputStream);
//
//            if (request.getRequestURI().startsWith("/servlet/")) {
//                Processor processor = new ServletProcessor();
//                processor.process(request, response);
//            } else {
//                Processor processor = new StaticResourceProcessor();
//                processor.process(request, response);
//            }
//            socket.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 解析HTTP请求中的请求行，并填充到HttpRequest对象的成员变量中。
     * parse()方法并不会解析请求体或查询字符串中的参数，这个任务由各个HttpRequest对象自己完成。
     *
     * @param inputStream  套接字的包装输入流
     * @param outputStream 套接字的输出流
     * @throws ServletException 抛出ServletException
     */
    private void parseRequest(SocketInputStream inputStream, OutputStream outputStream)
            throws ServletException, IOException {
        //解析请求行内容，使用SocketInputStream对象中的信息填充HttpRequestLine实例
        inputStream.readRequestLine(requestLine);

        //从请求行中获取请求方法、URI和请求协议的版本信息
        //第二个参数offset为子数组的第一个字符的索引，第三个参数为指定数组的长度
        String method = new String(requestLine.method, 0, requestLine.methodEnd);
        String uri;
        String protocol = new String(requestLine.protocol, 0, requestLine.protocolEnd);
        if (method.length() < 1) {
            throw new ServletException("没有HTTP请求方法");
        } else if (requestLine.uriEnd < 1) {
            throw new ServletException("没有HTTP请求的uri");
        }

        //从URI中解析查询字符串
        int question = requestLine.uriIndexOf("?");
        if (question >= 0) {
            //说明URI中带有查询字符串
            request.setQueryString(new String(requestLine.uri, question + 1,
                    requestLine.uriEnd - question - 1));
            uri = new String(requestLine.uri, 0, question);
        } else {
            request.setQueryString(null);
            uri = new String(requestLine.uri, 0, requestLine.uriEnd);
        }

        //判断URI是相对路径还是绝对路径
        if (!uri.startsWith("/")) {
            //如果不是以/开头，则这是一个绝对路径
            int pos = uri.indexOf("://");
            if (pos != -1) {
                //去除协议和主机名
                pos = uri.indexOf('/', pos + 3);
                if (pos == -1) {
                    uri = "";
                } else {
                    uri = uri.substring(pos);
                }
            } else {
                throw new ServletException("uri格式错误");
            }
        }

        //检查是否包含会话标识符，会话标识符的参数名为jsessionid，
        // 当jsessionid被找到，也意味着会话标识符是携带在查询字符串里边，
        // 而不是在cookie里边，setRequestedSessionURL()需要传递true
        String match = ";jsessionid=";
        int semicolon = uri.indexOf(match);
        if (semicolon >= 0) {
            //截取match后面的字符串
            String rest = uri.substring(semicolon + match.length());
            int semicolon2 = rest.indexOf(';');
            if (semicolon2 >= 0) {
                //截取match和下一个分号之间的字符串，即会话标识符
                request.setRequestedSessionId(rest.substring(0, semicolon2));
                rest = rest.substring(semicolon2);
            } else {
                //会话标识符之后没有其他的查询字符串了
                request.setRequestedSessionId(rest);
                rest = "";
            }
            request.setRequestedSessionURL(true);
            uri = uri.substring(0, semicolon) + rest;
        } else {
            request.setRequestedSessionId(null);
            request.setRequestedSessionURL(false);
        }

        //对uri进行规范检查以及修正
        String normalizedUri = RequestUtil.normalize(uri);
        request.setMethod(method);
        request.setProtocol(protocol);
        if (normalizedUri != null) {
            request.setRequestURI(normalizedUri);
        } else {
            request.setRequestURI(uri);
        }

        if (normalizedUri == null) {
            throw new ServletException("无效的URI: " + uri);
        }
    }

    /**
     * 解析了一些“简单”的请求首部，像"cookie", "content-length","content-type"，忽略了其他头部
     *
     * @param inputStream 包装后的套接字的输入字符流
     * @throws IOException      会抛出IO异常
     * @throws ServletException 会抛出Servlet异常
     */
    private void parseHeader(SocketInputStream inputStream)
            throws IOException, ServletException {
        while (true) {
            HttpHeader header = new HttpHeader();
            //读取首部信息，并填充HttpHeader对象
            inputStream.readHeader(header);
            //是否已经全部读完请求首部信息
            if (header.nameEnd == 0) {
                if (header.valueEnd == 0) {
                    return;
                } else {
                    throw new ServletException("无效的HTTP首部格式");
                }
            }

            //获取请求首部的名字和值
            String name = new String(header.name, 0, header.nameEnd);
            String value = new String(header.value, 0, header.valueEnd);
            request.addHeader(name, value);
            switch (name) {
                case "cookie":
                    Cookie[] cookies = RequestUtil.parseCookieHeader(value);
                    for (Cookie cookie : cookies) {
                        if (cookie.getName().equals("jsessionid")) {
                            if (!request.isRequestedSessionIdFromCookie()) {
                                request.setRequestedSessionId(cookie.getValue());
                                request.setRequestedSessionCookie(true);
                                request.setRequestedSessionURL(false);
                            }
                        }
                        request.addCookie(cookie);
                    }
                    break;
                case "content-length":
                    int n;
                    try {
                        n = Integer.parseInt(value);
                    } catch (Exception e) {
                        throw new ServletException("报文主体长度格式错误！");
                    }
                    request.setContentLength(n);
                    break;
                case "content-type":
                    request.setContentType(value);
                    break;
            }
        }
    }

    private void parseConnection(Socket socket) {

    }

}
