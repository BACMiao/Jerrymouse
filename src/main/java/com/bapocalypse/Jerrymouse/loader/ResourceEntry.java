package com.bapocalypse.Jerrymouse.loader;

import java.net.URL;
import java.security.cert.Certificate;
import java.util.jar.Manifest;

/**
 * @package: com.bapocalypse.Jerrymouse.loader
 * @Author: 陈淼
 * @Date: 2017/1/16
 * @Description: 资源实体类
 */
public class ResourceEntry {
    //最后一次修改日期
    public long lastModified = -1;
    //class文件的字节流
    public byte[] binaryContent = null;
    //类加载器
    public Class loadedClass = null;
    //被加载类的源URL
    public URL source = null;
    //被加载类的代码库URL
    public URL codeBase = null;
    //证明
    public Manifest manifest = null;
    //证书
    public Certificate[] certificates = null;
}
