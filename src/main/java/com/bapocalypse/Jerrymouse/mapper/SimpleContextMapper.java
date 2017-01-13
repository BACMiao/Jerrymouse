package com.bapocalypse.Jerrymouse.mapper;

import com.bapocalypse.Jerrymouse.container.Container;
import com.bapocalypse.Jerrymouse.container.SimpleContext;
import com.bapocalypse.Jerrymouse.container.Wrapper;
import com.bapocalypse.Jerrymouse.request.HttpRequestBase;

/**
 * @package: com.bapocalypse.Jerrymouse.mapper
 * @Author: 陈淼
 * @Date: 2017/1/12
 * @Description: 简单的映射器，帮助servlet容器（Context）选择一个子容器来处理某个指定的请求。
 */
public class SimpleContextMapper implements Mapper {
    private SimpleContext context = null;
    private String protocol = null;

    @Override
    public Container getContainer() {
        return context;
    }

    @Override
    public void setContainer(Container container) {
        if (!(container instanceof SimpleContext)) {
            throw new IllegalArgumentException("非法容器类型！");
        }
        context = (SimpleContext) container;
    }

    @Override
    public String getProtocol() {
        return protocol;
    }

    @Override
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * 返回要处理某个特定请求的子容器的实例
     *
     * @param request 需要处理的请求
     * @param update  是否更新
     * @return 处理某个特定请求的子容器的实例(Wrapper)
     */
    @Override
    public Container map(HttpRequestBase request, boolean update) {
        String contextPath = request.getContextPath();
        //获得请求的URI
        String requestURI = request.getRequestURI();
        String relativeURI = requestURI.substring(contextPath.length());
        Wrapper wrapper = null;
        //通过请求的URI获得相对应的Wrapper
        String name = context.findServletMapping(relativeURI);
        if (name != null) {
            wrapper = (Wrapper) context.findChild(name);
        }
        return wrapper;
    }
}
