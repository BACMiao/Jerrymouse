package com.bapocalypse.Jerrymouse.logger;

/**
 * @package: com.bapocalypse.Jerrymouse.logger
 * @Author: 陈淼
 * @Date: 2017/1/14
 * @Description: 日志记录器，将日志信息输出到标准错误
 */
public class SystemErrLogger extends LoggerBase {
    private static final String info =
            "com.bapocalypse.Jerrymouse.logger.SystemErrLogger/1.0";

    @Override
    public void log(String message) {
        System.err.println(message);
    }
}
