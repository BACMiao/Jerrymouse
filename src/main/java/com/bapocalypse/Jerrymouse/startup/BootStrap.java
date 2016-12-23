package com.bapocalypse.Jerrymouse.startup;

import com.bapocalypse.Jerrymouse.connector.http.HttpConnector;

/**
 * @package: com.bapocalypse.Jerrymouse.startup
 * @Author: 陈淼
 * @Date: 2016/12/20
 * @Description: 服务器的启动类，负责启动应用程序
 */
public class BootStrap {
    public static void main(String[] args) {
        HttpConnector connector = new HttpConnector();
        connector.start();
    }
}
