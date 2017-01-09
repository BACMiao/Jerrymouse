package com.bapocalypse.Jerrymouse.startup;

import com.bapocalypse.Jerrymouse.connector.http.HttpConnector;
import com.bapocalypse.Jerrymouse.container.Container;
import com.bapocalypse.Jerrymouse.container.SimpleContainer;

import java.io.IOException;

/**
 * @package: com.bapocalypse.Jerrymouse.startup
 * @Author: 陈淼
 * @Date: 2016/12/20
 * @Description: 服务器的启动类，负责启动应用程序
 */
public class BootStrap {
    public static void main(String[] args) {
        HttpConnector connector = new HttpConnector();
        Container container = new SimpleContainer();
        connector.setContainer(container);
        try {
            connector.initialize();
            connector.start();
            //控制台键入任何字符服务器停止
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
