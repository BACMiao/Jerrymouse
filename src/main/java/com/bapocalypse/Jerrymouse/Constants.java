package com.bapocalypse.Jerrymouse;

import java.io.File;

/**
 * @package: com.bapocalypse.Jerrymouse
 * @Author: 陈淼
 * @Date: 2016/12/17
 * @Description:
 */
public class Constants {
    //WEB_ROOT我们HTML和其他文件所在的地方，System.getProperty("user.dir")是指当前工作路径
    public static final String WEB_ROOT = System.getProperty("user.dir")
            + File.separator + "webroot";
}
