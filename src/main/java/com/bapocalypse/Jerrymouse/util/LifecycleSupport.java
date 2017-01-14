package com.bapocalypse.Jerrymouse.util;

import com.bapocalypse.Jerrymouse.lifecycle.Lifecycle;
import com.bapocalypse.Jerrymouse.lifecycle.LifecycleEvent;
import com.bapocalypse.Jerrymouse.listener.LifecycleListener;

import java.util.ArrayList;

/**
 * @package: com.bapocalypse.Jerrymouse.util
 * @Author: 陈淼
 * @Date: 2017/1/13
 * @Description: 生命周期工具类，用于帮助组件管理监听器，并触发相应的生命周期事件
 */
public final class LifecycleSupport {
    //生命周期
    private Lifecycle lifecycle = null;
    //存储所有生命周期监听器
    private final ArrayList<LifecycleListener> listeners = new ArrayList<>();

    public LifecycleSupport(Lifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    /**
     * 向监听器的列表添加一个事件监听器
     *
     * @param listener 需要加入列表的新的监听器
     */
    public void addLifecycleListener(LifecycleListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * 返回说有监听器列表
     *
     * @return 监听器列表
     */
    public ArrayList<LifecycleListener> findLifecycleListeners() {
        return listeners;
    }

    /**
     * 触发一个生命周期事件
     *
     * @param type 事件的类型
     * @param data 事件的数据
     */
    public void fireLifecycleEvent(String type, Object data) {
        //创建一个事件
        LifecycleEvent event = new LifecycleEvent(lifecycle, type, data);
        for (LifecycleListener listener : listeners) {
            listener.lifecycleEvent(event);
        }
    }

    /**
     * 从监听器的列表中移除指定的监听器
     *
     * @param listener 需要被移除的监听器
     */
    public void removeLifecycleListener(LifecycleListener listener) {
        synchronized (listeners) {
            for (LifecycleListener lifecycleListener : listeners) {
                if (lifecycleListener == listener) {
                    listeners.remove(listener);
                    break;
                }
            }
        }
    }
}
