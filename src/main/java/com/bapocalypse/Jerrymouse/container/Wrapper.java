package com.bapocalypse.Jerrymouse.container;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

/**
 * @package: com.bapocalypse.Jerrymouse.wrapper
 * @Author: 陈淼
 * @Date: 2017/1/11
 * @Description: Wrapper接口的实现类要负责管理基础servlet类的生命周期，即调用servlet的
 * init()、service()、destroy()等方法。Wrapper已经是最低级的servlet容器，因此不能再
 * 向其中添加子容器。
 */
public interface Wrapper extends Container {
    /**
     * 分配一个已经初始化的servlet实例
     *
     * @return 已经初始化的servlet实例
     * @throws ServletException 抛出servlet异常
     */
    Servlet allocate() throws ServletException;

    /**
     * 载入并初始化servlet类
     *
     * @throws ServletException 抛出servlet异常
     */
    void load() throws ServletException;

    /**
     * 返回Wrapper容器所管理的servlet的全限定名
     *
     * @return servlet的全限定名
     */
    String getServletClass();

    /**
     * 设置Wrapper容器所管理的servlet的全限定名
     *
     * @param servletClass servlet的全限定名
     */
    void setServletClass(String servletClass);

}
