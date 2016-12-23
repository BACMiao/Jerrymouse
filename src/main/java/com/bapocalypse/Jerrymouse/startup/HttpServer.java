package com.bapocalypse.Jerrymouse.startup;

import com.bapocalypse.Jerrymouse.processor.ServletProcessor;
import com.bapocalypse.Jerrymouse.processor.StaticResourceProcessor;
import com.bapocalypse.Jerrymouse.request.Request;
import com.bapocalypse.Jerrymouse.response.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @package: com.bapocalypse.Jerrymouse
 * @Author: 陈淼
 * @Date: 2016/12/16
 * @Description: Web服务器
 */
public class HttpServer {
    //关闭服务器命令
    private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";
    //收到的关闭命令
    private boolean shutdown = false;

    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        server.await();
    }

    private void await() {
        ServerSocket serverSocket = null;
        int port = 6040;
        try {
            serverSocket = new ServerSocket(port, 1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        while (!shutdown) {
            Socket socket = null;
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                socket = serverSocket.accept();
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
                //创建Request对象，并调用parse()解析inputStream中HTTP请求的原始数据
                Request request = new Request(inputStream);
                request.parse();
                //创建Response对象
                Response response = new Response(outputStream);
                response.setRequest(request);
                if (request.getUri().startsWith("/servlet/")) {
                    ServletProcessor processor = new ServletProcessor();
                    processor.process(request, response);
                } else {
                    StaticResourceProcessor processor = new StaticResourceProcessor();
                    processor.process(request, response);
                }
                //关闭套接字
                socket.close();
                //测试HTTP请求的URI是否是关闭命令，若是，设置shutdown为true，程序退出循环
                shutdown = request.getUri().equals(SHUTDOWN_COMMAND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}