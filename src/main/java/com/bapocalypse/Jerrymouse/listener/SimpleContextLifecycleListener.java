package com.bapocalypse.Jerrymouse.listener;

import com.bapocalypse.Jerrymouse.lifecycle.Lifecycle;
import com.bapocalypse.Jerrymouse.lifecycle.LifecycleEvent;

/**
 * @package: com.bapocalypse.Jerrymouse.listener
 * @Author: 陈淼
 * @Date: 2017/1/13
 * @Description: 生命周期监听器实现类
 */
public class SimpleContextLifecycleListener implements LifecycleListener {
    /**
     * 输出触发事件的类型
     *
     * @param event 发生的事件
     */
    @Override
    public void lifecycleEvent(LifecycleEvent event) {
        System.out.println("SimpleContextLifecycleListener的事件 " + event.getType());
        if (Lifecycle.START_EVENT.equals(event.getType())) {
            System.out.println("context开始");
        } else if (Lifecycle.STOP_EVENT.equals(event.getType())) {
            System.out.println("context结束");
        }
    }
}
