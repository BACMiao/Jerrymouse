package com.bapocalypse.Jerrymouse.response;

/**
 * @package: com.bapocalypse.Jerrymouse.response
 * @Author: 陈淼
 * @Date: 2017/1/7
 * @Description:
 */
public class HttpResponseImpl extends HttpResponseBase {
    protected boolean allowChunking;   //是否允许分块

    public boolean isAllowChunking() {
        return allowChunking;
    }

    public void setAllowChunking(boolean allowChunking) {
        this.allowChunking = allowChunking;
    }
}
