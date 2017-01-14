package com.bapocalypse.Jerrymouse.lifecycle;

import com.bapocalypse.Jerrymouse.exception.LifecycleException;
import com.bapocalypse.Jerrymouse.listener.LifecycleListener;

/**
 * @package: com.bapocalypse.Jerrymouse.lifecycle
 * @Author: 陈淼
 * @Date: 2017/1/13
 * @Description: Lifecycle接口，负责实现启动（关闭）一个组件就可以将所有的组件启动（关闭）
 */
public interface Lifecycle {
    //组件启动时触发的事件
    String START_EVENT = "start";
    String BEFORE_START_EVENT = "before_start";
    String AFTER_START_EVENT = "after_start";
    //组件关闭时触发的事件
    String STOP_EVENT = "stop";
    String BEFORE_STOP_EVENT = "before_stop";
    String AFTER_STOP_EVENT = "after_stop";

    /**
     * 添加生命周期监听器
     *
     * @param listener 需要添加的监听器
     */
    void addLifecycleListener(LifecycleListener listener);

    /**
     * 查找到所有生命周期监听器
     *
     * @return 生命周期监听器列表
     */
    LifecycleListener[] findLifecycleListeners();

    /**
     * 移除生命周期监听器
     *
     * @param listener 需要移除的监听器
     */
    void removeLifecycleListener(LifecycleListener listener);

    /**
     * 供其父组件调用，以实现对其进行启动
     *
     * @throws LifecycleException 抛出Lifecycle异常
     */
    void start() throws LifecycleException;

    /**
     * 供其父组件调用，以实现对其进行暂停
     *
     * @throws LifecycleException 抛出Lifecycle异常
     */
    void stop() throws LifecycleException;
}
