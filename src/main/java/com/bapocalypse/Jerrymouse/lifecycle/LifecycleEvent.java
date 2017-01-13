package com.bapocalypse.Jerrymouse.lifecycle;

import java.util.EventObject;

/**
 * @package: com.bapocalypse.Jerrymouse.lifecycle
 * @Author: 陈淼
 * @Date: 2017/1/13
 * @Description: 生命周期事件类
 */
public final class LifecycleEvent extends EventObject {
    private Object data = null;   //事件数据
    private Lifecycle lifecycle = null; //生命周期
    private String type = null;   //事件类型

    public LifecycleEvent(Lifecycle lifecycle, String type) {
        this(lifecycle, type, null);
    }

    public LifecycleEvent(Lifecycle lifecycle, String type, Object data) {
        super(lifecycle);
        this.data = data;
        this.lifecycle = lifecycle;
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public Lifecycle getLifecycle() {
        return lifecycle;
    }

    public String getType() {
        return type;
    }
}
