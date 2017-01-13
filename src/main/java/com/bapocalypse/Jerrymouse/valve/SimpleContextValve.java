package com.bapocalypse.Jerrymouse.valve;

import com.bapocalypse.Jerrymouse.container.*;
import com.bapocalypse.Jerrymouse.request.HttpRequestBase;
import com.bapocalypse.Jerrymouse.response.HttpResponseBase;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @package: com.bapocalypse.Jerrymouse.valve
 * @Author: 陈淼
 * @Date: 2017/1/12
 * @Description: 基础阀，专门用于处理对SimpleContext类的请求
 */
public class SimpleContextValve implements Valve, Contained {
    private Container container = null;

    public SimpleContextValve(Container container) {
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
        if (request == null || response == null) {
            return;
        }

        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();
        String relativeURI =
                requestURI.substring(contextPath.length()).toUpperCase();

        Context ct = (Context) getContainer();
        Wrapper wrapper;
        wrapper = (Wrapper) ct.map(request, true);
        if (wrapper == null) {
            return;
        }
        response.setContext(ct);
        wrapper.invoke(request, response);
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
