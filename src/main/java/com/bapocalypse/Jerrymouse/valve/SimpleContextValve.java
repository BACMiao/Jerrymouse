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
    private String info =
            "com.bapocalypse.Jerrymouse.valve.SimpleContextValve/1.0";  //该阀的信息
    private Container container = null;

    public SimpleContextValve(Container container) {
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
        if (request == null || response == null) {
            return;
        }
        Context ct = (Context) getContainer();
        //获得所需要访问servlet的Wrapper
        Wrapper wrapper = (Wrapper) ct.map(request, true);
        if (wrapper == null) {
            return;
        }
        response.setContext(ct);
        //wrapper开始处理
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
