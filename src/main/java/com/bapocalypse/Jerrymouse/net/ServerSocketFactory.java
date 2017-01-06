package com.bapocalypse.Jerrymouse.net;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * @package: com.bapocalypse.Jerrymouse.net
 * @Author: 陈淼
 * @Date: 2017/1/6
 * @Description: 服务器套接字工厂接口
 */
public interface ServerSocketFactory {
    /**
     * 创建绑定到特定端口的服务器套接字
     *
     * @param port 指定端口
     * @return 绑定特定端口的服务器套接字
     * @throws IOException 打开套接字时发生IO异常
     */
    ServerSocket createSocket(int port) throws IOException;

    /**
     * 利用指定的队列最大长度和端口号创建服务器套接字
     *
     * @param port    指定端口
     * @param backlog 队列的最大长度，若队列满时收到连接指示，则拒绝该连接。
     * @return 具有指定队列长度和端口号的的服务器套接字
     * @throws IOException 打开套接字时发生IO异常
     */
    ServerSocket createSocket(int port, int backlog) throws IOException;
}
