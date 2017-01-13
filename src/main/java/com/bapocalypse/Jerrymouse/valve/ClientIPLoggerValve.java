package com.bapocalypse.Jerrymouse.valve;

import com.bapocalypse.Jerrymouse.container.Contained;
import com.bapocalypse.Jerrymouse.container.Container;
import com.bapocalypse.Jerrymouse.request.HttpRequestBase;
import com.bapocalypse.Jerrymouse.response.HttpResponseBase;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import java.io.IOException;

/**
 * @package: com.bapocalypse.Jerrymouse.valve
 * @Author: 陈淼
 * @Date: 2017/1/12
 * @Description: 阀，在控制台中输出客户端的IP地址
 */
public class ClientIPLoggerValve implements Valve, Contained {
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
        System.out.println("Client IP Logger Valve");
        ServletRequest servletRequest = request;
        System.out.println(servletRequest.getRemoteAddr());
        System.out.println("------------------------------------");
    }
}
