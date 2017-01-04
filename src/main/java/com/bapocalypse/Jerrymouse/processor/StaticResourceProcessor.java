package com.bapocalypse.Jerrymouse.processor;

import com.bapocalypse.Jerrymouse.request.HttpRequest;
import com.bapocalypse.Jerrymouse.response.HttpResponse;

import java.io.IOException;

/**
 * @package: com.bapocalypse.Jerrymouse.processor
 * @Author: 陈淼
 * @Date: 2016/12/16
 * @Description: 用于处理对于对静态资源的请求
 */
public class StaticResourceProcessor implements Processor {

    @Override
    public void process(HttpRequest request, HttpResponse response) {
        try {
            response.sendStaticResource();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
