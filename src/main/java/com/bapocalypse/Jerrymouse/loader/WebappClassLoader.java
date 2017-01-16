package com.bapocalypse.Jerrymouse.loader;

import com.bapocalypse.Jerrymouse.exception.LifecycleException;
import com.bapocalypse.Jerrymouse.lifecycle.Lifecycle;
import com.bapocalypse.Jerrymouse.listener.LifecycleListener;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

/**
 * @package: com.bapocalypse.Jerrymouse.loader
 * @Author: 陈淼
 * @Date: 2017/1/15
 * @Description: Web应用程序中负责载入类的类载入器
 */
public class WebappClassLoader extends URLClassLoader implements Reloader, Lifecycle {
    //不允许载入指定的类
    private static final String[] triggers = {
            "javax.servlet.Servlet"
    };

    //不允许载入的特殊包及其子包下的类，也不会将载入类的任务委托给系统类载入器去执行
    private static final String[] packageTriggers = {
            "javax",
            "org.xml.sax",
            "orh.xml.dom"
    };

    public WebappClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public WebappClassLoader(URL[] urls) {
        super(urls);
    }

    public WebappClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }

    @Override
    public void addRepository(String repository) {

    }

    @Override
    public String[] findRepositories() {
        return new String[0];
    }

    @Override
    public boolean modified() {
        return false;
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
    public void start() throws LifecycleException {

    }

    @Override
    public void stop() throws LifecycleException {

    }
}
