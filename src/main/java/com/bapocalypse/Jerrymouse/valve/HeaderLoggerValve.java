package com.bapocalypse.Jerrymouse.valve;

import com.bapocalypse.Jerrymouse.container.Contained;
import com.bapocalypse.Jerrymouse.container.Container;
import com.bapocalypse.Jerrymouse.request.HttpRequestBase;
import com.bapocalypse.Jerrymouse.response.HttpResponseBase;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

/**
 * @package: com.bapocalypse.Jerrymouse.valve
 * @Author: 陈淼
 * @Date: 2017/1/12
 * @Description:
 */
public class HeaderLoggerValve implements Valve, Contained {
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
        return null;
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
