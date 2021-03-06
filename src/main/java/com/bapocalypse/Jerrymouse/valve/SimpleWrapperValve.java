package com.bapocalypse.Jerrymouse.valve;

import com.bapocalypse.Jerrymouse.container.Contained;
import com.bapocalypse.Jerrymouse.container.Container;
import com.bapocalypse.Jerrymouse.container.SimpleWrapper;
import com.bapocalypse.Jerrymouse.request.HttpRequestBase;
import com.bapocalypse.Jerrymouse.response.HttpResponseBase;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @package: com.bapocalypse.Jerrymouse.valve
 * @Author: 陈淼
 * @Date: 2017/1/12
 * @Description: 基础阀，专门用于处理对SimpleWrapper类的请求
 */
public class SimpleWrapperValve implements Valve, Contained {
    private String info =
            "com.bapocalypse.Jerrymouse.valve.SimpleWrapperValve/1.0";  //该阀的信息
    private Container container = null;

    public SimpleWrapperValve(Container container) {
        this.container = container;
    }

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public void invoke(HttpRequestBase request, HttpResponseBase response,
                       ValveContext context)
            throws IOException, ServletException {

        SimpleWrapper wrapper = (SimpleWrapper) getContainer();
        ServletRequest servletRequest = request;
        ServletResponse servletResponse = response;
        Servlet servlet;
        HttpServletRequest httpServletRequest = null;
        if (servletRequest instanceof HttpServletRequest) {
            httpServletRequest = (HttpServletRequest) servletRequest;
        }
        HttpServletResponse httpServletResponse = null;
        if (servletResponse instanceof HttpServletResponse) {
            httpServletResponse = (HttpServletResponse) servletResponse;
        }
        servlet = wrapper.allocate();
        if (httpServletRequest != null && httpServletResponse != null) {
            servlet.service(httpServletRequest, httpServletResponse);
        } else {
            servlet.service(servletRequest, servletResponse);
        }
    }

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }
}
