package com.bapocalypse.Jerrymouse.loader;

import com.bapocalypse.Jerrymouse.container.Container;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * @package: com.bapocalypse.Jerrymouse.loader
 * @Author: 陈淼
 * @Date: 2017/1/11
 * @Description: 载入器的接口，负责在servlet容器中载入相关的servlet类
 */
public interface Loader {
    /**
     * 获取ClassLoader类的实例
     *
     * @return ClassLoader类的实例
     */
    ClassLoader getClassLoader();

    /**
     * 获得与该载入器相关联的容器
     *
     * @return 关联的容器
     */
    Container getContainer();

    /**
     * 设置该载入器与容器相关联
     *
     * @param container 需要关联的容器
     */
    void setContainer(Container container);

    /**
     * 载入器的实现是否要委托给一个父类载入器
     *
     * @return 是否要委托给一个父类载入器
     */
    boolean getDelegate();

    /**
     * 设置载入器的实现是否要委托给一个父类载入器
     *
     * @param delegate 是否要委托给一个父类载入器
     */
    void setDelegate(boolean delegate);

    /**
     * 获得载入器的信息
     *
     * @return 载入器的信息
     */
    String getInfo();

    /**
     * 获得是否支持载入器的自动重载
     *
     * @return 是否支持
     */
    boolean getReloadable();

    /**
     * 设置是否支持载入器的自动重载
     *
     * @param reloadable 是否支持
     */
    void setReloadable(boolean reloadable);

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

    /**
     * 添加一个新的仓库，Web应用程序中的WEB-INF/classes目录和WEB-INF/lib目录
     * 是作为仓库添加到类载入器中的。
     *
     * @param repository 需要被添加到载入器中的仓库
     */
    void addRepository(String repository);

    /**
     * 获得所有已添加的仓库的链表对象
     *
     * @return 已添加的仓库的链表对象
     */
    ArrayList<String> findRepositories();

    /**
     * 如果Web应用程序中的某个servlet或相关类被修改了，modified()方法
     * 会返回true。
     *
     * @return 是否被修改了
     */
    boolean modified();
}
