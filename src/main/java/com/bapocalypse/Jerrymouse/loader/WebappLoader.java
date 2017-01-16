package com.bapocalypse.Jerrymouse.loader;

import com.bapocalypse.Jerrymouse.container.Container;
import com.bapocalypse.Jerrymouse.container.Context;
import com.bapocalypse.Jerrymouse.exception.LifecycleException;
import com.bapocalypse.Jerrymouse.lifecycle.Lifecycle;
import com.bapocalypse.Jerrymouse.listener.LifecycleListener;
import com.bapocalypse.Jerrymouse.util.LifecycleSupport;

import java.beans.PropertyChangeListener;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

/**
 * @package: com.bapocalypse.Jerrymouse.loader
 * @Author: 陈淼
 * @Date: 2017/1/15
 * @Description:
 */
public class WebappLoader implements Loader, Lifecycle, Runnable {
    private String loaderClass =
            "com.bapocalypse.Jerrymouse.loader.WebappClassLoader";  //指明类载入器的类的名字
    private static final String info =
            "com.bapocalypse.Jerrymouse.loader.WebappLoader/1.0";
    private LifecycleSupport lifecycle = new LifecycleSupport(this); //生命周期工具类
    private ClassLoader parentClassLoader = null;   //我们将要创建的加载器的父加载器
    private Container container = null;   //与该载入器相关联的容器
    private boolean started = false;  //指明WebappLoader实例是否已经启动
    private ArrayList<String> repositories = new ArrayList<>();  //仓库列表
    private WebappClassLoader classLoader = null;  //负责载入类的类载入器
    private Thread thread = null;    //后台线程
    private boolean threadDone = false;  //后台线程是否完成
    private int checkInterval = 15;   //检查间隔时间

    /**
     * 创建类载入器，可以使用setLoaderClass()方法改变将会创建的载入器。
     * 注意：自定义类载入器必须继承WebappClassLoader类。
     *
     * @return 创建完成后的载入器
     * @throws Exception 抛出异常
     */
    private WebappClassLoader createClassLoader() throws Exception {
        Class clazz = Class.forName(loaderClass);
        WebappClassLoader classLoader;
        if (parentClassLoader == null) {
            classLoader = (WebappClassLoader) clazz.newInstance();
        } else {
            Class[] argTypes = {ClassLoader.class};
            Object[] args = {parentClassLoader};
            //返回一个Constructor对象，它反映此Class对象所表示的类的指定公共构造方法。
            Constructor constructor = clazz.getConstructor(argTypes);
            //使用构造函数创建实例对象
            classLoader = (WebappClassLoader) constructor.newInstance(args);
        }
        return classLoader;
    }

    /**
     * 基于相关的Context容器为我们的类加载器配置相关的仓库
     */
    private void setRepositories() {
        String classesPath = "/WEB-INF/classes";  //项目代码的路径
        String libPath = "/WEB-INF/lib";     //jar包路径
        if (!(container instanceof Context)) {
            return;
        }
    }

    private void setClassPath() {

    }

    @Override
    public ClassLoader getClassLoader() {
        return null;
    }

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    public boolean getDelegate() {
        return false;
    }

    @Override
    public void setDelegate(boolean delegate) {

    }

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public boolean getReloadable() {
        return false;
    }

    @Override
    public void setReloadable(boolean reloadable) {

    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {

    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {

    }

    @Override
    public void addRepository(String repository) {
        if (repositories.contains(repository)) {
            return;
        }
        repositories.add(repository);
        if (started && (classLoader != null)) {
            classLoader.addRepository(repository);
            setClassPath();
        }
    }

    @Override
    public ArrayList<String> findRepositories() {
        return null;
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
        if (started) {
            throw new LifecycleException("WebappLoader已经启动！");
        }
        lifecycle.fireLifecycleEvent(START_EVENT, null);
        started = true;
        setRepositories();

    }

    @Override
    public void stop() throws LifecycleException {

    }

    /**
     * 支持自动重载
     */
    @Override
    public void run() {
        //不断循环直到结束标识被设置
        while (!threadDone) {
            //使线程休眠一段时间，时长由变量checkInterval指定，单位为秒
            threadSleep();
            if (!started) {
                break;
            }
            //调用WebappLoader实例的类载入器的modified()方法检测已经载入的类是否被修改
            if (!classLoader.modified()) {
                continue;
            }
            //若某个已经载入的类被修改了，则调用私有方法notifyContext()，
            // 通知与WebappLoader实例相关联的Context容器重新载入类
            notifyContext();
            break;
        }
    }

    private void threadSleep() {
        try {
            Thread.sleep(checkInterval * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 实例化WebappContextNotifier内部类，然后将其传入线程，并调用start方法，
     * 开始另一个线程处理重载相关类的任务。
     */
    private void notifyContext() {
        WebappContextNotifier notifier = new WebappContextNotifier();
        new Thread(notifier).start();
    }

    public String getLoaderClass() {
        return loaderClass;
    }

    public void setLoaderClass(String loaderClass) {
        this.loaderClass = loaderClass;
    }

    public int getCheckInterval() {
        return checkInterval;
    }

    public void setCheckInterval(int checkInterval) {
        this.checkInterval = checkInterval;
    }

    /**
     * 内部类，用于通知相关的Context容器重载相关的类
     */
    protected class WebappContextNotifier implements Runnable {
        @Override
        public void run() {
            ((Context) container).reload();
        }
    }
}
