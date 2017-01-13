package com.bapocalypse.Jerrymouse.container;

import com.bapocalypse.Jerrymouse.loader.Loader;
import com.bapocalypse.Jerrymouse.pipeline.Pipeline;
import com.bapocalypse.Jerrymouse.pipeline.SimplePipeline;
import com.bapocalypse.Jerrymouse.request.HttpRequestBase;
import com.bapocalypse.Jerrymouse.response.HttpResponseBase;
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
public class SimpleWrapper implements Wrapper {
    private Loader loader;   //指明了载入servlet类要使用的载入器
    private Container parent = null; //指明了该Wrapper实例的父容器
    private Pipeline pipeline = new SimplePipeline(this);
    private String servletClass = null;  //需要载入的servlet的全限定名
    private Servlet servlet;      //需要载入的servlet
    private String name = null;   //指定该Wrapper的名字

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
}
