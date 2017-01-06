package com.bapocalypse.Jerrymouse.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @package: com.bapocalypse.Jerrymouse.util
 * @Author: 陈淼
 * @Date: 2017/1/1
 * @Description: 获取参数
 */
public final class ParameterMap<K, V> extends HashMap<K, V> {
    private boolean locked = false;  //锁，只有其值为false时才能对ParameterMap对象中的键值对进行操作

    public ParameterMap() {
        super();
    }

    public ParameterMap(int initialCapacity) {
        super(initialCapacity);
    }

    public ParameterMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public ParameterMap(Map map) {
        super(map);
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override
    public void clear() {
        if (locked) {
            throw new IllegalStateException("参数映射锁定");
        }
        super.clear();
    }

    @Override
    public V put(K key, V value) {
        if (locked) {
            throw new IllegalStateException("参数映射锁定");
        }
        return super.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        if (locked) {
            throw new IllegalStateException("参数映射锁定");
        }
        super.putAll(m);
    }

    @Override
    public V remove(Object key) {
        if (locked) {
            throw new IllegalStateException("参数映射锁定");
        }
        return super.remove(key);
    }
}
