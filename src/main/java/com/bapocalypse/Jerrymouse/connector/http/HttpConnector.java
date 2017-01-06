package com.bapocalypse.Jerrymouse.connector.http;

import com.bapocalypse.Jerrymouse.connector.Connector;
import com.bapocalypse.Jerrymouse.connector.Container;
import com.bapocalypse.Jerrymouse.net.DefaultServerSocketFactory;
import com.bapocalypse.Jerrymouse.net.ServerSocketFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @package: com.bapocalypse.Jerrymouse.connector.http
 * @Author: 陈淼
 * @Date: 2016/12/20
 * @Description: 连接器类，负责创建一个服务器套接字，该套接字会等待传入的HTTP请求。
 */
public class HttpConnector implements Runnable, Connector {
    private boolean stopped;
    private String scheme = "http";  //请求协议

    //返回请求协议
    public String getScheme() {
        return scheme;
    }

    @Override
    public void initialize() {

    }

    @Override
    public void run() {
        ServerSocket serverSocket = null;
        int port = 6040;
        try {
            serverSocket = new ServerSocket(port, 1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        while (!stopped) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            //为每一个请求创建一个HttpProcessor对象
            HttpProcessor processor = new HttpProcessor();
            processor.process(socket);
        }
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void setContainer(Container container) {

    }

    @Override
    public Container getContainer() {
        return null;
    }

    @Override
    public void createRequest() {

    }

    @Override
    public void createResponse() {

    }

    @Override
    public void setSocketFactory(ServerSocketFactory ServerSocketFactory) {

    }

    @Override
    public ServerSocketFactory getSocketFactory() {
        return null;
    }
}
