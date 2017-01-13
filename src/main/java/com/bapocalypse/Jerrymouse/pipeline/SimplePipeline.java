package com.bapocalypse.Jerrymouse.pipeline;

import com.bapocalypse.Jerrymouse.container.Container;
import com.bapocalypse.Jerrymouse.request.HttpRequestBase;
import com.bapocalypse.Jerrymouse.response.HttpResponseBase;
import com.bapocalypse.Jerrymouse.valve.Valve;
import com.bapocalypse.Jerrymouse.valve.ValveContext;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @package: com.bapocalypse.Jerrymouse.pipeline
 * @Author: 陈淼
 * @Date: 2017/1/12
 * @Description: 简单的管道实现类，用于存放处理request对象和response对象的阀
 */
public class SimplePipeline implements Pipeline {
    private String info =
            "com.bapocalypse.Jerrymouse.pipeline.SimplePipeline/1.0";  //该管道的信息
    private final ArrayList<Valve> valves = new ArrayList<>(); //所有阀的链表
    private Valve basic = null;  //基础阀
    private Container container = null;  //被哪个容器所包含

    public SimplePipeline(Container container) {
        super();
        setContainer(container);
    }

    /**
     * 作为管道的一个内部类实现的，ValveContext可以访问管道中的所有成员
     */
    protected class StandardPipelineValveContext implements ValveContext {
        int stage = 0;

        @Override
        public String getInfo() {
            return info;
        }

        /**
         * 管道会调用该方法，invokeNext()首先会调用第一个阀，第一个阀执行完毕后，
         * 会调用后面的阀继续进行，同时也会将ValveContext自身传给每个阀，因此每
         * 个阀都可以调用该方法。
         *
         * @param request  HTTP请求对象
         * @param response HTTP响应对象
         * @throws IOException      抛出IO异常
         * @throws ServletException 抛出Servlet异常
         */
        @Override
        public void invokeNext(HttpRequestBase request, HttpResponseBase response)
                throws IOException, ServletException {
            int subscript = stage;
            stage += 1;
            if (subscript < valves.size()) {
                valves.get(subscript).invoke(request, response, this);
            } else if (subscript == valves.size() && basic != null) {
                basic.invoke(request, response, this);
            } else {
                throw new ServletException("该管道中没有阀！");
            }
        }
    }

    @Override
    public Valve getBasic() {
        return basic;
    }

    @Override
    public void setBasic(Valve valve) {
        basic = valve;
    }

    @Override
    public void addValve(Valve valve) {
        synchronized (valves) {
            valves.add(valve);
        }
    }

    @Override
    public ArrayList<Valve> getValves() {
        return valves;
    }

    @Override
    public void invoke(HttpRequestBase request, HttpResponseBase response) throws IOException, ServletException {
        new StandardPipelineValveContext().invokeNext(request, response);
    }

    @Override
    public void removeValve(Valve valve) {
        valves.remove(valve);
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }

    public Container getContainer() {
        return container;
    }
}
