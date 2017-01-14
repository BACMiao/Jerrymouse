package com.bapocalypse.Jerrymouse.startup;

import com.bapocalypse.Jerrymouse.connector.http.HttpConnector;
import com.bapocalypse.Jerrymouse.container.Context;
import com.bapocalypse.Jerrymouse.container.SimpleContext;
import com.bapocalypse.Jerrymouse.container.SimpleWrapper;
import com.bapocalypse.Jerrymouse.container.Wrapper;
import com.bapocalypse.Jerrymouse.exception.LifecycleException;
import com.bapocalypse.Jerrymouse.lifecycle.Lifecycle;
import com.bapocalypse.Jerrymouse.listener.SimpleContextLifecycleListener;
import com.bapocalypse.Jerrymouse.loader.Loader;
import com.bapocalypse.Jerrymouse.loader.SimpleLoader;
import com.bapocalypse.Jerrymouse.mapper.Mapper;
import com.bapocalypse.Jerrymouse.mapper.SimpleContextMapper;
import com.bapocalypse.Jerrymouse.valve.ClientIPLoggerValve;
import com.bapocalypse.Jerrymouse.valve.HeaderLoggerValve;
import com.bapocalypse.Jerrymouse.valve.Valve;

import java.io.IOException;

/**
 * @package: com.bapocalypse.Jerrymouse.startup
 * @Author: 陈淼
 * @Date: 2017/1/12
 * @Description: 启动器类
 */
public class BootStrap {
    public static void main(String[] args) {
        HttpConnector connector = new HttpConnector();
        Wrapper wrapper1 = new SimpleWrapper();
        wrapper1.setServletClass("com.HelloServlet");
        wrapper1.setName("Hello");
        Wrapper wrapper2 = new SimpleWrapper();
        wrapper2.setServletClass("ModernServlet");
        wrapper2.setName("Modern");

        Context context = new SimpleContext();
        Lifecycle lifecycle = (Lifecycle) context;
        context.setName("context");
        context.addChild(wrapper1);
        context.addChild(wrapper2);

        Valve valve1 = new HeaderLoggerValve();
        Valve valve2 = new ClientIPLoggerValve();

        context.addValve(valve1);
        context.addValve(valve2);

        Mapper mapper = new SimpleContextMapper();
        mapper.setProtocol("http");
        context.addMapper(mapper);
        Loader loader = new SimpleLoader();
        context.setLoader(loader);
        context.addServletMapping("/Hello", "Hello");
        context.addServletMapping("/Modern", "Modern");
        connector.setContainer(context);
        try {
            lifecycle.addLifecycleListener(new SimpleContextLifecycleListener());
            lifecycle.start();
            connector.initialize();
            connector.start();
            System.in.read();
            lifecycle.stop();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
    }
}
