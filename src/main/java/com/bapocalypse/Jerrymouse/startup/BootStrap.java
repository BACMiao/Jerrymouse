package com.bapocalypse.Jerrymouse.startup;

import com.bapocalypse.Jerrymouse.connector.http.HttpConnector;
import com.bapocalypse.Jerrymouse.container.SimpleWrapper;
import com.bapocalypse.Jerrymouse.container.Wrapper;
import com.bapocalypse.Jerrymouse.loader.Loader;
import com.bapocalypse.Jerrymouse.loader.SimpleLoader;
import com.bapocalypse.Jerrymouse.valve.ClientIPLoggerValve;
import com.bapocalypse.Jerrymouse.valve.HeaderLoggerValve;
import com.bapocalypse.Jerrymouse.valve.Valve;

import java.io.IOException;

/**
 * @package: com.bapocalypse.Jerrymouse.startup
 * @Author: 陈淼
 * @Date: 2017/1/12
 * @Description:
 */
public class BootStrap {
    public static void main(String[] args) {
        HttpConnector connector = new HttpConnector();
        Wrapper wrapper = new SimpleWrapper();
        wrapper.setServletClass("ModernServlet");
        Loader loader = new SimpleLoader();
        Valve valve1 = new HeaderLoggerValve();
        Valve valve2 = new ClientIPLoggerValve();

        wrapper.setLoader(loader);
        wrapper.addValve(valve1);
        wrapper.addValve(valve2);
        connector.setContainer(wrapper);

        try {
            connector.initialize();
            connector.start();
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
