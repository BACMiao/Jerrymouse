package com.bapocalypse.Jerrymouse.logger;

import com.bapocalypse.Jerrymouse.exception.LifecycleException;
import com.bapocalypse.Jerrymouse.lifecycle.Lifecycle;
import com.bapocalypse.Jerrymouse.listener.LifecycleListener;
import com.bapocalypse.Jerrymouse.util.LifecycleSupport;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;


/**
 * @package: com.bapocalypse.Jerrymouse.logger
 * @Author: 陈淼
 * @Date: 2017/1/14
 * @Description: 日志记录器，将日志信息写到一个文件中中，并且可以选择是否要为每条消息添加时间戳。
 * 当该类首次被实例化时，会创建一个日志文件，文件名包含当日的日志信息。若日期发生了变化，则创建一个
 * 新文件，并将所有的日志消息都写到新文件中。使用该类实例的时候，可以在日志文件的名称中添加前缀和后缀。
 */
public class FileLogger extends LoggerBase implements Lifecycle {
    private static final String info =
            "com.bapocalypse.Jerrymouse.logger.FileLogger/1.0";
    private boolean started = false;  //指明FileLogger实例是否已经启动
    private LifecycleSupport lifecycle = new LifecycleSupport(this); //生命周期工具类
    private String date = "";   //日期字符串
    private PrintWriter writer = null;
    private boolean isTimestamp = false;
    private String directory = ""; //存储文件的目录
    private String prefix = "";    //文件的首部部分
    private String suffix = "";    //文件的尾部部分

    /**
     * 会将接收到的日志消息写到一个日志文件中。在FileLogger实例的整个生命周期中，
     * log()方法可能打开/关闭多个日志文件。
     *
     * @param message 需要记录的字符串
     */
    @Override
    public void log(String message) {
        //获取当前日期
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        //获取当前日期的字符串表示形式，toString()返回格式:yyyy-mm-dd hh:mm :ss.fffffffff
        String tsString = timestamp.toString().substring(0, 19);
        //获取日期部分
        String tsDate = tsString.substring(0, 10);

        if (!date.equals(tsDate)) {
            synchronized (this) {
                if (!date.equals(tsDate)) {
                    close();
                    date = tsDate;
                    open();
                }
            }
        }

        //PrintWriter使用输出流写入到日志文件中
        if (writer != null) {
            if (isTimestamp) {
                //添加时间戳
                writer.println(tsString + " " + message);
            } else {
                writer.println(message);
            }
        }
    }

    @Override
    public void addLifecycleListener(LifecycleListener listener) {

    }

    @Override
    public LifecycleListener[] findLifecycleListeners() {
        return new LifecycleListener[0];
    }

    @Override
    public void removeLifecycleListener(LifecycleListener listener) {

    }

    /**
     * 启动文件日志记录器时触发生命周期事件
     *
     * @throws LifecycleException 抛出Lifecycle异常
     */
    @Override
    public void start() throws LifecycleException {
        if (started) {
            throw new LifecycleException("文件日志记录器已经启动！");
        }
        lifecycle.fireLifecycleEvent(START_EVENT, null);
        started = true;
    }

    /**
     * 关闭文件日志记录器时触发生命周期事件
     *
     * @throws LifecycleException 抛出Lifecycle异常
     */
    @Override
    public void stop() throws LifecycleException {
        if (!started) {
            throw new LifecycleException("文件日志记录器已经关闭！");
        }
        lifecycle.fireLifecycleEvent(STOP_EVENT, null);
        started = false;
        close();
    }

    /**
     * 在指定目录创建一个新的日志文件
     */
    private void open() {
        File dir = new File(directory);
        //检查文件目录是否是绝对路径名
        if (!dir.isAbsolute()) {
            dir = new File(System.getProperty("Jerrymouse.base"), directory);
        }
        //创建此抽象路径名指定的目录，包括所有必需但不存在的父目录 todo
        dir.mkdirs();

        try {
            //根据待打开日志文件的目录路径，创建位于指定位置的日志文件
            String pathname = dir.getAbsolutePath() + File.separator +
                    prefix + date + suffix;
            //记录日志消息，FileWriter的第二个参数为true，则将字节写入文件末尾处
            writer = new PrintWriter(new FileWriter(pathname, true), true);
        } catch (IOException e) {
            writer = null;
        }

    }

    /**
     * 关闭打开的日志文件，并负责确保将PrintWriter实例中所有的日志消息都写入到文件中。
     */
    private void close() {
        if (writer != null) {
            writer.flush();
            writer.close();
            writer = null;
            date = "";
        }
    }

    public void setTimestamp(boolean timestamp) {
        isTimestamp = timestamp;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
