package com.bapocalypse.Jerrymouse.loader;

import com.bapocalypse.Jerrymouse.container.Container;
import com.bapocalypse.Jerrymouse.util.Constants;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

/**
 * @package: com.bapocalypse.Jerrymouse.loader
 * @Author: 陈淼
 * @Date: 2017/1/12
 * @Description: 简单的加载类，负责完成类的载入工作
 */
public class SimpleLoader implements Loader {
    private ClassLoader classLoader = null;
    private Container container = null;

    /**
     * 该构造函数会初始化类加载器，供SimpleWrapper实例使用
     */
    public SimpleLoader() {
        try {
            URL[] urls = new URL[1];
            URLStreamHandler streamHandler = null;
            File classPath = new File(Constants.WEB_ROOT);
            //仓库路径，指定协议为文件
            String repository =
                    (new URL("file", null, classPath.getCanonicalPath() +
                            File.separator)).toString();
            //类载入器只需要查找一个位置
            urls[0] = new URL(null, repository, streamHandler);
            //urls中的URL指明类载入器要到哪里查找类，若URL以“/”结尾，则表明它指向的是一个目录，
            // 否则，URL默认指向一个JAR文件
            classLoader = new URLClassLoader(urls);
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }
}
