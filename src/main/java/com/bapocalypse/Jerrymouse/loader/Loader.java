package com.bapocalypse.Jerrymouse.loader;

/**
 * @package: com.bapocalypse.Jerrymouse.loader
 * @Author: 陈淼
 * @Date: 2017/1/11
 * @Description: 载入器的接口，负责在servlet容器中载入相关的servlet类
 */
public interface Loader {
    ClassLoader getClassLoader();
}
