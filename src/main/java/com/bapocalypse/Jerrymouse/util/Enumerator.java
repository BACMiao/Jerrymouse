package com.bapocalypse.Jerrymouse.util;

import java.util.*;

/**
 * @package: com.bapocalypse.Jerrymouse.util
 * @Author: 陈淼
 * @Date: 2017/1/5
 * @Description: 适配器模式，一个适配了枚举类的迭代器
 */
public class Enumerator<T> implements Enumeration<T> {
    private Iterator<T> iterator;

    public Enumerator(Iterator<T> iterator) {
        super();
        this.iterator = iterator;
    }

    public Enumerator(Collection<T> collection) {
        this(collection.iterator());
    }

    /**
     * 该构造函数是用以迭代map的键名
     *
     * @param map 需要迭代的map
     */
    public Enumerator(Map map) {
        //todo
        this(map.keySet().iterator());
    }



    @Override
    public boolean hasMoreElements() {
        return iterator.hasNext();
    }

    @Override
    public T nextElement() {
        return iterator.next();
    }
}
