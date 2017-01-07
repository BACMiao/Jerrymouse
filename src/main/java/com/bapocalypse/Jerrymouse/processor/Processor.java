package com.bapocalypse.Jerrymouse.processor;

import com.bapocalypse.Jerrymouse.request.HttpRequestBase;
import com.bapocalypse.Jerrymouse.response.HttpResponseBase;

/**
 * @package: com.bapocalypse.Jerrymouse.processor
 * @Author: 陈淼
 * @Date: 2017/1/4
 * @Description: 处理HTTP请求的接口
 */
public interface Processor {
    void process(HttpRequestBase request, HttpResponseBase response);
}
