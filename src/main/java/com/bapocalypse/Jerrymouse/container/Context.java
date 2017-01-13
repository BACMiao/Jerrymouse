package com.bapocalypse.Jerrymouse.container;

import com.bapocalypse.Jerrymouse.mapper.Mapper;
import com.bapocalypse.Jerrymouse.request.HttpRequestBase;

/**
 * @package: com.bapocalypse.Jerrymouse.container
 * @Author: 陈淼
 * @Date: 2017/1/12
 * @Description: Context接口的实例表示一个Web应用程序。一个Context可以有多个Wrapper
 */
public interface Context extends Container {

    /**
     * 添加一个URL模式/Wrapper实例的名称对，通过给定的名称添加用于调用Wrapper实例的每种模式
     *
     * @param pattern     URL模式
     * @param wrapperName Wrapper实例名
     */
    void addServletMapping(String pattern, String wrapperName);

    /**
     * 通过URL模式查找对应的Wrapper实例名称
     *
     * @param pattern 指定的URL模式
     * @return 返回与指定URL模式对应的Wrapper实例
     */
    String findServletMapping(String pattern);

    /**
     * 向Context中添加Wrapper
     *
     * @param wrapper 需要添加的Wrapper
     */
    void addWrapper(Wrapper wrapper);

    /**
     * 创建阀
     */
    void createWrapper();

    /**
     * 映射器，返回负责处理当前请求的Wrapper实例
     *
     * @param request 特定的HTTP请求
     * @param update  是否更新
     * @return 要处理的某个特定请求的子容器的实例
     */
    Container map(HttpRequestBase request, boolean update);

    /**
     * 给该容器添加映射器
     *
     * @param mapper 需要添加的映射器
     */
    void addMapper(Mapper mapper);

    /**
     * 找到正确的映射器
     *
     * @param protocol 特定的协议
     * @return 特定协议所对应的映射器
     */
    Mapper findMapper(String protocol);
}
