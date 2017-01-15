package com.bapocalypse.Jerrymouse.loader;

/**
 * @package: com.bapocalypse.Jerrymouse.loader
 * @Author: 陈淼
 * @Date: 2017/1/15
 * @Description: 自动重载接口，重载器
 */
public interface Reloader {
    /**
     * 向重载器中添加相关的仓库
     *
     * @param repository 需要被添加的仓库
     */
    void addRepository(String repository);

    /**
     * 返回实现重载器接口的类的所有仓库的数组对象
     *
     * @return 所有仓库的数组对象
     */
    String[] findRepositories();

    /**
     * 如果Web应用程序中的某个servlet或相关类被修改了，modified()方法
     * 会返回true。
     *
     * @return 相关类是否被修改了
     */
    boolean modified();
}
