package com.bapocalypse.Jerrymouse.container;

/**
 * @package: com.bapocalypse.Jerrymouse.container
 * @Author: 陈淼
 * @Date: 2017/1/12
 * @Description: 包含类接口，该接口的实现类可以通过接口中的方法至多与一个servlet容器相关联
 */
public interface Contained {
    /**
     * 获得与之相关联的servlet容器
     *
     * @return 相关联的servlet容器
     */
    Container getContainer();

    /**
     * 设置servlet容器与实现该接口的类相关联
     *
     * @param container 需要关联的servlet容器
     */
    void setContainer(Container container);
}
