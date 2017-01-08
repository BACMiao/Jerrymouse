package com.bapocalypse.Jerrymouse.response;

import com.bapocalypse.Jerrymouse.connector.http.HttpConnector;

/**
 * @package: com.bapocalypse.Jerrymouse.response
 * @Author: 陈淼
 * @Date: 2017/1/7
 * @Description:
 */
public class HttpResponseImpl extends HttpResponseBase {
    private boolean allowChunking;   //是否允许分块
    private HttpConnector connector = null;

    public boolean isAllowChunking() {
        return allowChunking;
    }

    public void setAllowChunking(boolean allowChunking) {
        this.allowChunking = allowChunking;
    }

    public HttpConnector getConnector() {
        return connector;
    }

    public void setConnector(HttpConnector connector) {
        this.connector = connector;
    }
}
