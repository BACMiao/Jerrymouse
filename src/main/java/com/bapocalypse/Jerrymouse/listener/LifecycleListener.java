package com.bapocalypse.Jerrymouse.listener;

import com.bapocalypse.Jerrymouse.lifecycle.LifecycleEvent;

/**
 * @package: com.bapocalypse.Jerrymouse.lifecycle
 * @Author: 陈淼
 * @Date: 2017/1/13
 * @Description: 生命周期的事件监听器接口
 */
public interface LifecycleListener {
    /**
     * 当某个事件监听器监听到相关事件发生，会调用该方法
     *
     * @param event 发生的事件
     */
    void lifecycleEvent(LifecycleEvent event);
}
