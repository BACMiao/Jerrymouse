package com.bapocalypse.Jerrymouse.Processor;

import com.bapocalypse.Jerrymouse.Constants;
import com.bapocalypse.Jerrymouse.Request;
import com.bapocalypse.Jerrymouse.Response;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

/**
 * @package: com.bapocalypse.Jerrymouse.Processor
 * @Author: 陈淼
 * @Date: 2016/12/16
 * @Description: 用于处理对servlet资源的HTTP请求
 */
public class ServletProcessor {

    public void process(Request request, Response response) {
        String uri = request.getUri();
        String servletName = uri.substring(uri.lastIndexOf("/") + 1);
        URLClassLoader loader = null;
        URL[] urls = new URL[1];
        URLStreamHandler streamHandler = null;
        File classPath = new File(Constants.WEB_ROOT);
        try {
            String repository =
                    (new URL("file", null, classPath.getCanonicalPath() +
                            File.separator)).toString();
            urls[0] = new URL(null, repository, streamHandler);
            loader = new URLClassLoader(urls);
        } catch (IOException e) {
            System.out.println(e.toString());
        }

        Class myClass = null;
        try {
            myClass = loader.loadClass(servletName);//todo
        } catch (ClassNotFoundException e) {
            System.out.println(e.toString());
        }

        Servlet servlet = null;
        try {
            if (myClass != null) {
                servlet = (Servlet) myClass.newInstance();
                servlet.service(request, response);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
