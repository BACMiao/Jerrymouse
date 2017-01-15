package com.bapocalypse.Jerrymouse.logger;

import com.bapocalypse.Jerrymouse.container.Container;

import java.beans.PropertyChangeListener;

/**
 * @package: com.bapocalypse.Jerrymouse.logger
 * @Author: 陈淼
 * @Date: 2017/1/14
 * @Description: 日志接口，Jerrymouse中的日志记录器都必须实现该接口
 */
public interface Logger {
    //日志级别
    int FATAL = Integer.MIN_VALUE;
    int ERROR = 1;
    int WARNING = 2;
    int INFORMATION = 3;
    int DEBUG = 4;

    /**
     * 获得与该日志记录器相关联的servlet容器
     *
     * @return 与之关联的servlet容器
     */
    Container getContainer();

    /**
     * 将该日志记录器与和某个servlet容器相关联
     *
     * @param container 需要与日志记录器相关联的servlet容器
     */
    void setContainer(Container container);

    /**
     * 得到该日志记录器的信息
     *
     * @return 该日志记录器的信息
     */
    String getInfo();

    /**
     * 获取日志级别
     *
     * @return 日志级别
     */
    int getVerbosity();

    /**
     * 设置日志级别
     *
     * @param verbosity 日志的级别
     */
    void setVerbosity(int verbosity);

    /**
     * 用于写日志
     *
     * @param message 需要记录的字符串
     */
    void log(String message);

    /**
     * 用于写日志
     *
     * @param exception 需要记录的异常
     * @param msg       异常相关的信息
     */
    void log(Exception exception, String msg);

    /**
     * 用于写日志
     *
     * @param message   用于描述异常或错误的信息
     * @param throwable 异常或者错误
     */
    void log(String message, Throwable throwable);

    /**
     * 用于写日志
     *
     * @param message   需要被写入日志的信息
     * @param verbosity 日志级别参数
     */
    void log(String message, int verbosity);

    /**
     * 用于写日志
     *
     * @param message   用于描述异常或错误的信息
     * @param throwable 异常或者错误
     * @param verbosity 日志级别参数
     */
    void log(String message, Throwable throwable, int verbosity);

    /**
     * 添加PropertyChangeListener实例
     *
     * @param listener 需要添加的PropertyChangeListener实例
     */
    void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * 移除PropertyChangeListener实例
     *
     * @param listener 需要移除的PropertyChangeListener实例
     */
    void removePropertyChangeListener(PropertyChangeListener listener);
}
