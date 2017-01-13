package com.bapocalypse.Jerrymouse.container;

import com.bapocalypse.Jerrymouse.loader.Loader;
import com.bapocalypse.Jerrymouse.mapper.Mapper;
import com.bapocalypse.Jerrymouse.pipeline.Pipeline;
import com.bapocalypse.Jerrymouse.pipeline.SimplePipeline;
import com.bapocalypse.Jerrymouse.request.HttpRequestBase;
import com.bapocalypse.Jerrymouse.response.HttpResponseBase;
import com.bapocalypse.Jerrymouse.valve.SimpleContextValve;
import com.bapocalypse.Jerrymouse.valve.Valve;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;

/**
 * @package: com.bapocalypse.Jerrymouse.container
 * @Author: 陈淼
 * @Date: 2017/1/12
 * @Description: 简单的Context类，实现了Context接口，包含多个Wrapper实例
 */
public class SimpleContext implements Context {
    private Loader loader;   //指明了载入servlet类要使用的载入器
    private Container parent = null; //指明了该Context实例的父容器
    private Pipeline pipeline = new SimplePipeline(this); //指明了该Context所包含的管道
    private Mapper mapper = null;    //指明了该容器所使用的映射器
    private final HashMap<String, Mapper> mappers = new HashMap<>();   //该容器所包含的映射器列表
    private HashMap<String, String> servletMapping = new HashMap<>();  //URL模式/Wrapper实例的名称对
    private String name = null;   //指定了该Context的名字
    private final HashMap<String, Container> children = new HashMap<>(); //子容器列表

    public SimpleContext() {
        pipeline.setBasic(new SimpleContextValve(this));
    }

    @Override
    public void addServletMapping(String pattern, String wrapperName) {
        servletMapping.put(pattern, wrapperName);
    }

    @Override
    public String findServletMapping(String pattern) {
        return servletMapping.get(pattern);
    }

    @Override
    public void addWrapper(Wrapper wrapper) {

    }

    @Override
    public void createWrapper() {

    }

    @Override
    public Container map(HttpRequestBase request, boolean update) {
        return mapper.map(request, update);
    }

    @Override
    public void addMapper(Mapper mapper) {
        synchronized (mappers) {
            if (mappers.get(mapper.getProtocol()) != null) {
                throw new IllegalArgumentException("addMapper:该协议'" +
                        mapper.getProtocol() + "'已经存在");
            }
            mapper.setContainer(this);
            mappers.put(mapper.getProtocol(), mapper);
            if (mappers.size() == 1) {
                this.mapper = mapper;
            }
        }
    }

    /**
     * 返回的是默认映射器
     *
     * @param protocol 特定的协议
     * @return 默认映射器
     */
    @Override
    public Mapper findMapper(String protocol) {
        return mappers.get(protocol);
    }

    @Override
    public void invoke(HttpRequestBase request, HttpResponseBase response)
            throws IOException, ServletException {
        pipeline.invoke(request, response);
    }

    @Override
    public void addChild(Container container) {
        synchronized (children) {
            if (children.get(container.getName()) != null) {
                throw new IllegalArgumentException("addChild:子容器'" +
                        container.getName() + "'已经存在了");
            } else {
                container.setParent(this);
                children.put(container.getName(), container);
            }
        }
    }

    @Override
    public void removeChild(Container container) {
        synchronized (children) {
            if (children.get(container.getName()) == null) {
                throw new IllegalArgumentException("addChild:子容器'" +
                        container.getName() + "'不存在存在了");
            }
            container.setParent(null);
            children.remove(container.getName());
        }
    }

    @Override
    public Container findChild(String name) {
        synchronized (children) {
            if (children.get(name) == null) {
                throw new IllegalArgumentException("addChild:子容器'" +
                        name + "'不存在存在了");
            }
            return children.get(name);
        }
    }

    @Override
    public Container[] findChildren() {
        return children.values().toArray(new Container[children.values().size()]);
    }

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

    @Override
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
