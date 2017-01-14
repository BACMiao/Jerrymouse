package com.bapocalypse.Jerrymouse.container;

import com.bapocalypse.Jerrymouse.exception.LifecycleException;
import com.bapocalypse.Jerrymouse.lifecycle.Lifecycle;
import com.bapocalypse.Jerrymouse.listener.LifecycleListener;
import com.bapocalypse.Jerrymouse.loader.Loader;
import com.bapocalypse.Jerrymouse.pipeline.Pipeline;
import com.bapocalypse.Jerrymouse.pipeline.SimplePipeline;
import com.bapocalypse.Jerrymouse.request.HttpRequestBase;
import com.bapocalypse.Jerrymouse.response.HttpResponseBase;
import com.bapocalypse.Jerrymouse.util.LifecycleSupport;
import com.bapocalypse.Jerrymouse.valve.SimpleWrapperValve;
import com.bapocalypse.Jerrymouse.valve.Valve;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @package: com.bapocalypse.Jerrymouse.container
 * @Author: 陈淼
 * @Date: 2017/1/12
 * @Description: 简单的Wrapper类，用于载入所包含的servlet类
 */
public class SimpleWrapper implements Wrapper, Lifecycle {
    private Loader loader;   //指明了载入servlet类要使用的载入器
    private Container parent = null; //指明了该Wrapper实例的父容器
    private Pipeline pipeline = new SimplePipeline(this);
    private String servletClass = null;  //需要载入的servlet的全限定名
    private Servlet servlet;      //需要载入的servlet
    private String name = null;   //指定该Wrapper的名字
    private boolean started = false; //该Wrapper是否启动
    private LifecycleSupport lifecycle = new LifecycleSupport(this); //生命周期工具类

    public SimpleWrapper() {
        pipeline.setBasic(new SimpleWrapperValve(this));
    }

    @Override
    public void invoke(HttpRequestBase request, HttpResponseBase response)
            throws IOException, ServletException {
        pipeline.invoke(request, response);
    }

    @Override
    public void addChild(Container container) {
        throw new IllegalArgumentException("Wrapper已经是最小容器了，不能添加子容器！");
    }

    @Override
    public void removeChild(Container container) {
        throw new IllegalArgumentException("Wrapper已经是最小容器了");
    }

    @Override
    public Container findChild(String name) {
        throw new IllegalArgumentException("Wrapper已经是最小容器了");
    }

    @Override
    public Container[] findChildren() {
        throw new IllegalArgumentException("Wrapper已经是最小容器了");
    }

    @Override
    public Servlet allocate() throws ServletException {
        load();
        return servlet;
    }

    @Override
    public void load() throws ServletException {
        Class myClass;
        try {
            myClass = getLoader().getClassLoader().loadClass(servletClass);
            servlet = (Servlet) myClass.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getServletClass() {
        return servletClass;
    }

    @Override
    public void setServletClass(String servletClass) {
        this.servletClass = servletClass;
    }

    /**
     * 返回一个用于载入servlet类的载入器，若容器实例已经关联一个载入器，
     * 则直接将其返回；否则，它将返回父容器的载入器。若没有父容器，
     * getLoader()方法会返回null。
     *
     * @return 返回一个载入器
     */
    @Override
    public Loader getLoader() {
        if (loader != null) {
            return loader;
        }
        if (parent != null) {
            return parent.getLoader();
        }
        return null;
    }

    public void setLoader(Loader loader) {
        this.loader = loader;
    }

    @Override
    public void addValve(Valve valve) {
        pipeline.addValve(valve);
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setParent(Container parent) {
        this.parent = parent;
    }

    @Override
    public Container getParent() {
        return parent;
    }

    @Override
    public void addLifecycleListener(LifecycleListener listener) {

    }

    @Override
    public LifecycleListener[] findLifecycleListeners() {
        return new LifecycleListener[0];
    }

    @Override
    public void removeLifecycleListener(LifecycleListener listener) {

    }

    @Override
    public synchronized void start() throws LifecycleException {
        System.out.println("启动Wrapper:" + name);
        if (started) {
            throw new LifecycleException("Wrapper已经启动！");
        }
        lifecycle.fireLifecycleEvent(BEFORE_START_EVENT, null);
        started = true;
        if (loader != null && loader instanceof Lifecycle) {
            ((Lifecycle) loader).start();
        }

        if (pipeline instanceof Lifecycle) {
            ((Lifecycle) pipeline).start();
        }

        lifecycle.fireLifecycleEvent(START_EVENT, null);
        lifecycle.fireLifecycleEvent(AFTER_START_EVENT, null);
    }

    @Override
    public void stop() throws LifecycleException {
        System.out.println("关闭Wrapper:" + name);
        try {
            servlet.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        servlet = null;
        if (!started) {
            throw new LifecycleException("Wrapper" + name + "已经关闭！");
        }
        lifecycle.fireLifecycleEvent(STOP_EVENT, null);
        lifecycle.fireLifecycleEvent(AFTER_STOP_EVENT, null);
        started = false;

        if (pipeline instanceof Lifecycle) {
            ((Lifecycle) pipeline).stop();
        }

        if (loader != null && loader instanceof Lifecycle) {
            ((Lifecycle) loader).stop();
        }
        lifecycle.fireLifecycleEvent(AFTER_STOP_EVENT, null);
    }
}
