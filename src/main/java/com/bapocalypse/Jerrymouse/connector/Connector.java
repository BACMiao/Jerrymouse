package com.bapocalypse.Jerrymouse.connector;

import com.bapocalypse.Jerrymouse.net.ServerSocketFactory;

/**
 * @package: com.bapocalypse.Jerrymouse.connector
 * @Author: 陈淼
 * @Date: 2017/1/6
 * @Description:
 */
public interface Connector {
    /**
     * 用于将连接器和某个servlet容器相关联
     *
     * @param container 传入连接的servlet容器
     */
    void setContainer(Container container);

    /**
     * @return 当前连接器相关联的servlet容器
     */
    Container getContainer();

    /**
     * 为引入的HTTP请求创建request对象
     */
    void createRequest();

    /**
     * 为引入的HTTP请求创建response对象
     */
    void createResponse();

    /**
     * 传入一个服务器套接字工厂给当前容器使用
     *
     * @param ServerSocketFactory 服务器套接字工厂
     */
    void setSocketFactory(ServerSocketFactory ServerSocketFactory);

    /**
     * 得到当前容器使用的服务器套接字工厂
     *
     * @return 当前容器使用的服务器套接字工厂
     */
    ServerSocketFactory getSocketFactory();

    /**
     * 初始化，并绑定端口号
     */
    void initialize();
}
