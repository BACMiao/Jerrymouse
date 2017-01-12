package com.bapocalypse.Jerrymouse.pipeline;

import com.bapocalypse.Jerrymouse.container.Container;
import com.bapocalypse.Jerrymouse.request.HttpRequestBase;
import com.bapocalypse.Jerrymouse.response.HttpResponseBase;
import com.bapocalypse.Jerrymouse.valve.Valve;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @package: com.bapocalypse.Jerrymouse.pipeline
 * @Author: 陈淼
 * @Date: 2017/1/11
 * @Description: 包含该servlet容器将要调用的任务，一个阀表示一个具体的执行任务。
 * 在管道中有一个基础阀，但可以在其中添加任意数量的阀。阀的数量指的是额外添加阀的数量（不包括基础阀）。
 * 管道就像是servlet编程的过滤器链。
 */
public interface Pipeline {
    /**
     * 与此管道相关联的容器
     *
     * @param container 相关联的容器
     */
    void setContainer(Container container);

    /**
     * 获得被塞入管道的基础阀
     *
     * @return 一个基础阀
     */
    Valve getBasic();

    /**
     * 将基础阀设置到该管道中
     *
     * @param valve 需要被塞入管道的基础阀
     */
    void setBasic(Valve valve);

    /**
     * 向管道中添加新的阀
     *
     * @param valve 需要被塞入管道的新阀
     */
    void addValve(Valve valve);

    /**
     * 获得所有被塞入该管道中的阀（基础阀除外）
     *
     * @return 一个阀的数组
     */
    ArrayList<Valve> getValves();

    /**
     * servlet容器会调用这个invoke()来开始调用管道中的阀和基础阀
     *
     * @param request  需要被处理的HTTP请求对象
     * @param response 需要被处理的HTTP响应对象
     * @throws IOException      抛出IO异常
     * @throws ServletException 抛出Servlet异常
     */
    void invoke(HttpRequestBase request, HttpResponseBase response)
            throws IOException, ServletException;

    /**
     * 从管道中删除某个阀
     *
     * @param valve 需要被移除的阀
     */
    void removeValve(Valve valve);
}
