package com.bapocalypse.Jerrymouse.container;

import com.bapocalypse.Jerrymouse.loader.Loader;
import com.bapocalypse.Jerrymouse.pipeline.Pipeline;
import com.bapocalypse.Jerrymouse.pipeline.SimplePipeline;
import com.bapocalypse.Jerrymouse.request.HttpRequestBase;
import com.bapocalypse.Jerrymouse.response.HttpResponseBase;
import com.bapocalypse.Jerrymouse.valve.SimpleWrapperValve;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

/**
 * @package: com.bapocalypse.Jerrymouse.container
 * @Author: 陈淼
 * @Date: 2017/1/12
 * @Description:
 */
public class SimpleWrapper implements Wrapper {
    private Loader loader;   //指明了载入servlet类要使用的载入器
    protected Container parent = null; //指明了该Wrapper实例的父容器
    private Pipeline pipeline = new SimplePipeline(this);

    public SimpleWrapper() {
        pipeline.setBasic(new SimpleWrapperValve());
    }

    @Override
    public void invoke(HttpRequestBase request, HttpResponseBase response) {

    }

    @Override
    public void addChild(Container container) {

    }

    @Override
    public void removeChild(Container container) {

    }

    @Override
    public Container findChild(String name) {
        return null;
    }

    @Override
    public Container[] findChildren() {
        return new Container[0];
    }

    @Override
    public Servlet allocate() throws ServletException {
        return null;
    }

    @Override
    public void load() throws ServletException {

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
}
