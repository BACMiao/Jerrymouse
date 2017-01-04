package com.bapocalypse.Jerrymouse.processor;

import com.bapocalypse.Jerrymouse.request.HttpRequest;
import com.bapocalypse.Jerrymouse.response.HttpResponse;

/**
 * @package: com.bapocalypse.Jerrymouse.processor
 * @Author: 陈淼
 * @Date: 2017/1/4
 * @Description: 处理HTTP请求的接口
 */
public interface Processor {
    void process(HttpRequest request, HttpResponse response);
}
