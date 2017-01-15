package com.bapocalypse.Jerrymouse.container;

import com.bapocalypse.Jerrymouse.exception.LifecycleException;
import com.bapocalypse.Jerrymouse.lifecycle.Lifecycle;
import com.bapocalypse.Jerrymouse.listener.LifecycleListener;
import com.bapocalypse.Jerrymouse.loader.Loader;
import com.bapocalypse.Jerrymouse.logger.FileLogger;
import com.bapocalypse.Jerrymouse.logger.Logger;
import com.bapocalypse.Jerrymouse.mapper.Mapper;
import com.bapocalypse.Jerrymouse.pipeline.Pipeline;
import com.bapocalypse.Jerrymouse.pipeline.SimplePipeline;
import com.bapocalypse.Jerrymouse.request.HttpRequestBase;
import com.bapocalypse.Jerrymouse.response.HttpResponseBase;
import com.bapocalypse.Jerrymouse.util.LifecycleSupport;
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
public class SimpleContext implements Context, Lifecycle {
    private Loader loader;   //指明了载入servlet类要使用的载入器
    private Container parent = null; //指明了该Context实例的父容器
    private Pipeline pipeline = new SimplePipeline(this); //指明了该Context所包含的管道
    private Mapper mapper = null;    //指明了该容器所使用的映射器
    private final HashMap<String, Mapper> mappers = new HashMap<>();   //该容器所包含的映射器列表
    private HashMap<String, String> servletMapping = new HashMap<>();  //URL模式/Wrapper实例的名称对
    private String name = null;   //指定了该Context的名字
    private final HashMap<String, Container> children = new HashMap<>(); //子容器列表
    private LifecycleSupport lifecycle = new LifecycleSupport(this); //生命周期工具类
    private boolean started = false;  //指明SimpleContext实例是否已经启动
    private Logger logger = null;   //与当前context相关联的日志记录器

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
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public void invoke(HttpRequestBase request, HttpResponseBase response)
            throws IOException, ServletException {
        logger.log("simpleContext.invoke: " + this.getName());
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

    @Override
    public void addLifecycleListener(LifecycleListener listener) {
        lifecycle.addLifecycleListener(listener);
    }

    @Override
    public LifecycleListener[] findLifecycleListeners() {
        return null;
    }

    @Override
    public void removeLifecycleListener(LifecycleListener listener) {
        lifecycle.removeLifecycleListener(listener);
    }

    /**
     * 启动所有子容器以及相关的组件，包括载入器和映射器，使用单一启动/关闭机制，
     * 使用这种机制，只需要启动最高层级的组件即可，其余的组件会由各自的父组件去启动。
     *
     * @throws LifecycleException 抛出Lifecycle异常
     */
    @Override
    public synchronized void start() throws LifecycleException {
        //检查该组件是否启动过了
        if (started) {
            throw new LifecycleException("SimpleContext 已经启动！");
        }
        //触发BEFORE_START_EVENT事件，SimpleContext实例中对该事件进行监听
        // 的所有监听器都会收到通知。
        lifecycle.fireLifecycleEvent(BEFORE_START_EVENT, null);
        started = true;
        //启动它的组件和子容器
        try {
            if (logger != null && logger instanceof FileLogger) {
                ((FileLogger) logger).start();
            }
            if (loader != null && loader instanceof Lifecycle) {
                ((Lifecycle) loader).start();
            }

            Container[] children = findChildren();
            for (Container child : children) {
                if (child instanceof Lifecycle) {
                    ((Lifecycle) child).start();
                }
            }

            if (pipeline instanceof Lifecycle) {
                ((Lifecycle) pipeline).start();
            }
            //触发START_EVENT事件
            lifecycle.fireLifecycleEvent(START_EVENT, null);
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
        //触发AFTER_START_EVENT事件
        lifecycle.fireLifecycleEvent(AFTER_START_EVENT, null);
    }

    /**
     * 关闭所有子容器以及相关的组件，包括载入器和映射器
     *
     * @throws LifecycleException 抛出Lifecycle异常
     */
    @Override
    public void stop() throws LifecycleException {
        //检查该组件是否已经关闭
        if (!started) {
            throw new LifecycleException("SimpleContext已经被关闭！");
        }
        //触发BEFORE_STOP_EVENT事件
        lifecycle.fireLifecycleEvent(BEFORE_STOP_EVENT, null);
        //触发STOP_EVENT事件
        lifecycle.fireLifecycleEvent(STOP_EVENT, null);
        started = false;
        //关闭所关联的所有组件和子容器
        try {
            if (pipeline instanceof Lifecycle) {
                ((Lifecycle) pipeline).stop();
            }

            Container[] children = findChildren();
            for (Container child : children) {
                if (child instanceof Lifecycle) {
                    ((Lifecycle) child).stop();
                }
            }

            if (loader != null && loader instanceof Lifecycle) {
                ((Lifecycle) loader).stop();
            }

            if (logger != null && logger instanceof FileLogger) {
                ((FileLogger) logger).stop();
            }
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
        //触发AFTER_STOP_EVENT事件
        lifecycle.fireLifecycleEvent(AFTER_STOP_EVENT, null);
    }
}
