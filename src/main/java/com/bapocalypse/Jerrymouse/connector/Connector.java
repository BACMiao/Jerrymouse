package com.bapocalypse.Jerrymouse.connector;

import com.bapocalypse.Jerrymouse.connector.http.HttpProcessor;
import com.bapocalypse.Jerrymouse.net.ServerSocketFactory;
import com.bapocalypse.Jerrymouse.request.HttpRequestBase;
import com.bapocalypse.Jerrymouse.request.HttpRequestImpl;
import com.bapocalypse.Jerrymouse.response.HttpResponseBase;
import com.bapocalypse.Jerrymouse.response.HttpResponseImpl;

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
     *
     * @return 返回创建的request对象
     */
    HttpRequestImpl createRequest();

    /**
     * 为引入的HTTP请求创建response对象
     *
     * @return 返回创建的response对象
     */
    HttpResponseImpl createResponse();

    /**
     * 传入一个服务器套接字工厂给当前容器使用
     *
     * @param socketFactory 服务器套接字工厂
     */
    void setSocketFactory(ServerSocketFactory socketFactory);

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

    /**
     * 将新创建的HttpProcessor对象压入栈
     *
     * @param processor 新创建的HttpProcessor对象
     */
    void recycle(HttpProcessor processor);
}
