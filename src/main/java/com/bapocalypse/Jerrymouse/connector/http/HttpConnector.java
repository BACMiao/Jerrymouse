package com.bapocalypse.Jerrymouse.connector.http;

import com.bapocalypse.Jerrymouse.processor.HttpProcessor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @package: com.bapocalypse.Jerrymouse.connector.http
 * @Author: 陈淼
 * @Date: 2016/12/20
 * @Description: 连接器类，负责创建一个服务器套接字，该套接字会等待传入的HTTP请求。
 */
public class HttpConnector implements Runnable {
    private boolean stopped;
    private String scheme = "http";  //请求协议

    //返回请求协议
    public String getScheme() {
        return scheme;
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
            HttpProcessor processor = new HttpProcessor();
            processor.process(socket);
        }
    }

     public void  start(){
        Thread thread = new Thread(this);
        thread.start();
     }
}
