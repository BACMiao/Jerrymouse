package com.bapocalypse.Jerrymouse.Processor;

import com.bapocalypse.Jerrymouse.Request;
import com.bapocalypse.Jerrymouse.Response;

import java.io.IOException;

/**
 * @package: com.bapocalypse.Jerrymouse.Processor
 * @Author: 陈淼
 * @Date: 2016/12/16
 * @Description: 用于处理对于对静态资源的请求
 */
public class StaticResourceProcessor {

    public void process(Request request, Response response) {
        try {
            response.sendStaticResource();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
