package com.bapocalypse.Jerrymouse.valve;

import com.bapocalypse.Jerrymouse.request.HttpRequestBase;
import com.bapocalypse.Jerrymouse.response.HttpResponseBase;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @package: com.bapocalypse.Jerrymouse.valve
 * @Author: 陈淼
 * @Date: 2017/1/11
 * @Description: 实现阀的遍历执行
 */
public interface ValveContext {
    /**
     * 返回ValveContext的实现信息
     *
     * @return ValveContext的实现信息
     */
    String getInfo();

    /**
     * 首先先调用管道中的第一个阀，第一个阀执行完毕之后，会调用后面的阀继续执行。
     * 保证添加到管道中的所有阀和基础阀都被调用一次。
     *
     * @param request  HTTP请求对象
     * @param response HTTP响应对象
     * @throws IOException      抛出IO异常
     * @throws ServletException 抛出Servlet异常
     */
    void invokeNext(HttpRequestBase request, HttpResponseBase response)
            throws IOException, ServletException;
}
