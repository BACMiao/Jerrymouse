package com.bapocalypse.Jerrymouse.request;

import com.bapocalypse.Jerrymouse.connector.http.HttpConnector;

import java.net.InetAddress;

/**
 * @package: com.bapocalypse.Jerrymouse.request
 * @Author: 陈淼
 * @Date: 2017/1/7
 * @Description:
 */
public class HttpRequestImpl extends HttpRequestBase {
    private HttpConnector connector = null;
    private InetAddress inet = null;

    public void finishRequest() {
    }

    public HttpConnector getConnector() {
        return connector;
    }

    public void setConnector(HttpConnector connector) {
        this.connector = connector;
    }

    public InetAddress getInet() {
        return inet;
    }

    public void setInet(InetAddress inet) {
        this.inet = inet;
    }
}
