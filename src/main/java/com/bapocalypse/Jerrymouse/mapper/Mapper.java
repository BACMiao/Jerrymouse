package com.bapocalypse.Jerrymouse.mapper;

import com.bapocalypse.Jerrymouse.container.Container;
import com.bapocalypse.Jerrymouse.request.HttpRequestBase;

/**
 * @package: com.bapocalypse.Jerrymouse.mapper
 * @Author: 陈淼
 * @Date: 2017/1/11
 * @Description: 映射器，当应用程序有多个Wrapper实例时，需要一个映射器。映射器是组件，
 * 帮助servlet容器（Context）选择一个子容器来处理某个指定的请求。
 */
public interface Mapper {
    /**
     * 获得与该映射器相关联的Servlet容器的实例
     *
     * @return 与该映射器相关联的Servlet容器
     */
    Container getContainer();

    /**
     * 将某个servlet容器与该映射器相关联
     *
     * @param container 需要关联的servlet容器
     */
    void setContainer(Container container);

    /**
     * 获得该映射器负责处理的协议
     *
     * @return 该映射器负责处理的协议
     */
    String getProtocol();

    /**
     * 指定该映射器负责处理那种协议
     *
     * @param protocol 需要处理的协议
     */
    void setProtocol(String protocol);

    /**
     * 返回要处理某个特定请求的子容器的实例
     *
     * @param request 需要处理的请求
     * @param update  是否更新
     * @return 处理某个特定请求的子容器的实例
     */
    Container map(HttpRequestBase request, boolean update);
}
