package com.bapocalypse.Jerrymouse.valve;

import com.bapocalypse.Jerrymouse.container.Contained;
import com.bapocalypse.Jerrymouse.container.Container;
import com.bapocalypse.Jerrymouse.request.HttpRequestBase;
import com.bapocalypse.Jerrymouse.response.HttpResponseBase;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;

/**
 * @package: com.bapocalypse.Jerrymouse.valve
 * @Author: 陈淼
 * @Date: 2017/1/12
 * @Description: 阀，在控制台中输出请求首部信息
 */
public class HeaderLoggerValve implements Valve, Contained {
    private String info =
            "com.bapocalypse.Jerrymouse.valve.HeaderLoggerValve/1.0";  //该阀的信息
    private Container container;

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public void setContainer(Container container) {
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

        context.invokeNext(request, response);
        System.out.println("Header Logger Valve");
        ServletRequest servletRequest = request;
        if (servletRequest instanceof HttpServletRequest) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            Enumeration headerNames = httpServletRequest.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement().toString();
                String headerValue = httpServletRequest.getHeader(headerName);
                System.out.println(headerName + ":" + headerValue);
            }
        } else {
            System.out.println("Not an HTTP Request");
        }
        System.out.println("-------------------------------");
    }
}
