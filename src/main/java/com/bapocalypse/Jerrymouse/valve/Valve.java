package com.bapocalypse.Jerrymouse.valve;

import com.bapocalypse.Jerrymouse.request.HttpRequestBase;
import com.bapocalypse.Jerrymouse.response.HttpResponseBase;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @package: com.bapocalypse.Jerrymouse.valve
 * @Author: 陈淼
 * @Date: 2017/1/11
 * @Description: 阀，用于处理传递给它的request对象和response对象
 */
public interface Valve {
    /**
     * 返回阀的实现信息
     *
     * @return 阀的实现信息
     */
    String getInfo();

    /**
     * 处理传递给它的request对象和response对象
     *
     * @param request  HTTP请求对象
     * @param response HTTP响应对象
     * @param context  ValveContext对象，用于实现阀的遍历执行
     * @throws IOException      抛出IO异常
     * @throws ServletException 抛出Servlet异常
     */
    void invoke(HttpRequestBase request, HttpResponseBase response, ValveContext context)
            throws IOException, ServletException;
}
