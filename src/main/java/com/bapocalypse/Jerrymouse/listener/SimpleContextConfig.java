package com.bapocalypse.Jerrymouse.listener;

import com.bapocalypse.Jerrymouse.container.Context;
import com.bapocalypse.Jerrymouse.lifecycle.Lifecycle;
import com.bapocalypse.Jerrymouse.lifecycle.LifecycleEvent;

/**
 * @package: com.bapocalypse.Jerrymouse.listener
 * @Author: 陈淼
 * @Date: 2017/1/16
 * @Description:
 */
public class SimpleContextConfig implements LifecycleListener {
    @Override
    public void lifecycleEvent(LifecycleEvent event) {
        if (Lifecycle.START_EVENT.equals(event.getType())) {
            Context context = (Context) event.getLifecycle();
            context.setConfigured(true);
        }
    }
}
