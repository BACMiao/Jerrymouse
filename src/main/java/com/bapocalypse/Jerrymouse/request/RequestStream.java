package com.bapocalypse.Jerrymouse.request;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.IOException;

/**
 * @package: com.bapocalypse.Jerrymouse.request
 * @Author: 陈淼
 * @Date: 2017/1/2
 * @Description:
 */
public class RequestStream extends ServletInputStream {

    public RequestStream(HttpRequest request) {
        super();
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setReadListener(ReadListener readListener) {

    }

    @Override
    public int read() throws IOException {
        return 0;
    }
}
