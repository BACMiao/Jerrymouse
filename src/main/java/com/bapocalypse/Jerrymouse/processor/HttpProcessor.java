package com.bapocalypse.Jerrymouse.processor;

import com.bapocalypse.Jerrymouse.connector.http.HttpRequestLine;
import com.bapocalypse.Jerrymouse.connector.http.SocketInputStream;
import com.bapocalypse.Jerrymouse.request.HttpRequest;
import com.bapocalypse.Jerrymouse.request.Request;
import com.bapocalypse.Jerrymouse.response.Response;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @package: com.bapocalypse.Jerrymouse.processor
 * @Author: 陈淼
 * @Date: 2016/12/21
 * @Description: 处理器类，负责创建Request和Response对象
 */
public class HttpProcessor {
    private HttpRequestLine requestLine = new HttpRequestLine();
    private Request request;
    private Response response;

    /**
     * @param socket 接收到的套接字对象
     */
    public void process(Socket socket) {
        OutputStream outputStream = null; //返回此套接字的输出流
        InputStream inputStream = null;   //返回此套接字的输入流 todo
        try {
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
            request = new Request(inputStream); //todo
            response = new Response(outputStream);//todo
            response.setRequest(request);
            //todo
            if (request.getUri().startsWith("/servlet/")) {
                ServletProcessor processor = new ServletProcessor();
                processor.process(request, response);
            } else {
                StaticResourceProcessor processor = new StaticResourceProcessor();
                processor.process(request, response);
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析HTTP请求中的请求行和请求头信息，并填充到HttpRequest对象的成员变量中。
     * parse()方法并不会解析请求体或查询字符串中的参数，这个任务由各个HttpRequest对象自己完成。
     * @param inputStream
     * @param outputStream
     * @throws ServletException
     */
    private void parseRequest(SocketInputStream inputStream, OutputStream outputStream)
            throws ServletException {
        inputStream.readRequestLine(requestLine);
        String method = new String(requestLine.method, 0, requestLine.methodEnd);
        String uri = null;
        String protocol = new String(requestLine.protocol, 0, requestLine.protocolEnd);
        if (method.length() < 1) {
            throw new ServletException("Missing HTTP request method");
        } else if (requestLine.uriEnd < 1) {
            throw new ServletException("Missing HTTP request uri");
        }

        int question = requestLine.index("?");
        if (question >= 0) {
            //todo
            uri = new String(requestLine.uri, 0, question);
        } else {
            //todo
            uri = new String(requestLine.uri, 0, requestLine.uriEnd);

            if (!uri.startsWith("/")) {
                int pos = uri.indexOf("://");
                if (pos != -1) {
                    pos = uri.indexOf('/', pos + 3);
                    if (pos == -1){
                        uri = "";
                    } else {
                        uri = uri.substring(pos);
                    }
                }
            }
        }

        String match = ";jsessionid=";
        int semicolon = uri.indexOf(match);
        if (semicolon >= 0){
            String rest = uri.substring(semicolon + match.length());
            int semicolon2 = rest.indexOf(';');
            if (semicolon2 >= 0){
                //todo
                rest = rest.substring(semicolon2);
            } else {
                //todo
                rest = "";
            }
            //todo
            uri = uri.substring(0, semicolon) + rest;
        } else {
            //todo
        }

        String normalizedUri = normalize(uri);
        //todo
    }

    private String normalize(String uri){
        return null;
    }
}
