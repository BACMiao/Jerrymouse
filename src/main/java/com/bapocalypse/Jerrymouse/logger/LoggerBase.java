package com.bapocalypse.Jerrymouse.logger;

import com.bapocalypse.Jerrymouse.container.Container;

import java.beans.PropertyChangeListener;

/**
 * @package: com.bapocalypse.Jerrymouse.logger
 * @Author: 陈淼
 * @Date: 2017/1/14
 * @Description: 日志记录器的基础类，是一个抽象类。它实现了Logger接口中
 * 除log(String message)方法外的全部方法。
 */
public abstract class LoggerBase implements Logger {
    private Container container = null;   //当前与日志记录器相关联的servlet容器
    private int verbosity = ERROR;   //当前日志记录器的等级
    private String info =
            "com.bapocalypse.Jerrymouse.logger.LoggerBase/1.0";

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public int getVerbosity() {
        return verbosity;
    }

    @Override
    public void setVerbosity(int verbosity) {
        this.verbosity = verbosity;
    }

    /**
     * 该方法将在子类中给出具体的实现，并有多种重载方法。
     *
     * @param message 需要记录的字符串
     */
    @Override
    public abstract void log(String message);

    @Override
    public void log(Exception exception, String msg) {

    }

    @Override
    public void log(String message, Throwable throwable) {

    }

    @Override
    public void log(String message, int verbosity) {
        //只有当传入的日志等级比当前实例中verbosity变量指定的等级低时，才会记录日志
        if (this.verbosity >= verbosity) {
            log(message);
        }
    }

    @Override
    public void log(String message, Throwable throwable, int verbosity) {
        //只有当传入的日志等级比当前实例中verbosity变量指定的等级低时，才会记录日志
        if (this.verbosity >= verbosity) {
            log(message, throwable);
        }
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {

    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {

    }
}
