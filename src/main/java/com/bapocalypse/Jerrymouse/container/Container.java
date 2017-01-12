package com.bapocalypse.Jerrymouse.container;

import com.bapocalypse.Jerrymouse.loader.Loader;
import com.bapocalypse.Jerrymouse.request.HttpRequestBase;
import com.bapocalypse.Jerrymouse.response.HttpResponseBase;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @package: com.bapocalypse.Jerrymouse.connector
 * @Author: 陈淼
 * @Date: 2017/1/6
 * @Description: servlet容器的接口类
 */
public interface Container {
    /**
     * servlet容器会载入相应的类，调用其service()方法，管理session对象，记录错误消息等操作
     *
     * @param request  HTTP请求对象
     * @param response HTTP响应对象
     */
    void invoke(HttpRequestBase request, HttpResponseBase response) throws IOException, ServletException;

    /**
     * 向该容器中添加子容器
     *
     * @param container 需要添加的子容器
     */
    void addChild(Container container);

    /**
     * 删除该容器中的子容器
     *
     * @param container 需要被删除的子容器
     */
    void removeChild(Container container);

    /**
     * 查找名字为name的子容器
     *
     * @param name 容器名
     * @return 返回名字为name的子容器
     */
    Container findChild(String name);

    /**
     * 查找所有子容器的集合
     *
     * @return 该容器中所有子容器的集合
     */
    Container[] findChildren();

    /**
     * 返回一个用于载入servlet类的载入器，若容器实例已经关联一个载入器，
     * 则直接将其返回；否则，它将返回父容器的载入器。若没有父容器，
     * getLoader()方法会返回null。
     *
     * @return 返回一个载入器
     */
    Loader getLoader();

    /**
     * 传入需要使用载入器
     *
     * @param loader 需要使用的载入器
     */
    void setLoader(Loader loader);


}
