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
        //URLClassLoader类加载器用于从指向 JAR 文件和目录的 URL 的搜索路径加载类和资源。
        URLClassLoader loader = null;
        URL[] urls = new URL[1];
        URLStreamHandler streamHandler = null;
        File classPath = new File(Constants.WEB_ROOT);
        try {
            //仓库路径，指定协议为文件
            String repository =
                    (new URL("file", null, classPath.getCanonicalPath() +
                            File.separator)).toString();
            //类载入器只需要查找一个位置
            urls[0] = new URL(null, repository, streamHandler);
            //urls中的URL指明类载入器要到哪里查找类，若URL以“/”结尾，则表明它指向的是一个目录，
            // 否则，URL默认指向一个JAR文件
            loader = new URLClassLoader(urls);
        } catch (IOException e) {
            System.out.println(e.toString());
        }

        Class myClass = null;
        try {
            /*
              使用loadClass()方法载入子类，注意载入的必须是.class文件，
              如果是Java源文件，会发生ClassNotFoundException，
              如果.class文件有包名package a.b.c，
              则servletName需要在前面加上包名，即a.b.c.servletName，
              则此时的搜索路径为"file:WEB_ROOT/a/b/c/servletName.class"
              若无，则此时的搜索路径为"file:WEB_ROOT/servletName.class"
             */
            if (loader != null) {
                myClass = loader.loadClass(servletName);
            }
        } catch (ClassNotFoundException e) {
            System.out.println(e.toString());
        }

        Servlet servlet = null;
        try {
            if (myClass != null) {
                //创建servlet类的一个新实例
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
