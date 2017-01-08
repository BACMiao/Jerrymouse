package com.bapocalypse.Jerrymouse.container;

import com.bapocalypse.Jerrymouse.request.HttpRequestBase;
import com.bapocalypse.Jerrymouse.response.HttpResponseBase;

/**
 * @package: com.bapocalypse.Jerrymouse.connector
 * @Author: 陈淼
 * @Date: 2017/1/6
 * @Description: servlet容器的接口类
 */
public interface Container {
    /**
     * servlet容器会载入相应的类，调用其service()方法，管理session对象，记录错误消息等操作
     *
     * @param request  HTTP请求对象
     * @param response HTTP响应对象
     */
    void invoke(HttpRequestBase request, HttpResponseBase response);
}
