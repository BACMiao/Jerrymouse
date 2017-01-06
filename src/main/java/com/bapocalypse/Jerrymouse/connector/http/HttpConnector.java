package com.bapocalypse.Jerrymouse.connector.http;

import com.bapocalypse.Jerrymouse.connector.Connector;
import com.bapocalypse.Jerrymouse.connector.Container;
import com.bapocalypse.Jerrymouse.net.DefaultServerSocketFactory;
import com.bapocalypse.Jerrymouse.net.ServerSocketFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Stack;

/**
 * @package: com.bapocalypse.Jerrymouse.connector.http
 * @Author: 陈淼
 * @Date: 2016/12/20
 * @Description: 连接器类，负责创建一个服务器套接字，该套接字会等待传入的HTTP请求。
 */
public class HttpConnector implements Runnable, Connector {
    private ServerSocketFactory socketFactory = null; //服务器套接字工厂
    private int port = 6040;                          //端口号
    private int backlog = 1;                          //队列的最大长度
    private boolean initialized = false;              //该连接器是否进行了初始化
    private ServerSocket serverSocket = null;         //服务器套接字
    private Stack<HttpProcessor> processors = new Stack<>();           //用于存储HttpProcessor实例
    private boolean stopped;                          //钩子，用于停止循环

    private int minProcessors = 5;   //HttpProcessor实例的最少个数
    private int maxProcessors = 20;  //HttpProcessor实例的最多个数
    private int curProcessors = 0;   //HttpProcessor实例的当前的个数

    /**
     * 创建服务器套接字
     */
    @Override
    public void initialize() {
        if (initialized) {
            throw new IllegalStateException("该连接器已经进行初始化");
        }
        initialized = true;
        try {
            serverSocket = open();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * 从服务器套接字工厂得到一个实例
     *
     * @return 指定参数的服务器套接字
     * @throws IOException 打开套接字时发生IO异常
     */
    private ServerSocket open() throws IOException {
        ServerSocketFactory serverSocketFactory = getSocketFactory();
        if (backlog != 0) {
            return serverSocketFactory.createSocket(port, backlog);
        } else {
            return serverSocketFactory.createSocket(port);
        }
    }

    /**
     * 不断接收客户端的请求，并为每一个请求创建HttpProcessor对象
     */
    @Override
    public void run() {
        while (!stopped) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            HttpProcessor processor = new HttpProcessor(this, 0);
            processor.process(socket);
        }
    }

    /**
     * 启动后台处理守护线程
     */
    private void threadStart() {
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

    public void start() {
        threadStart();
        //为每一个请求创建一个HttpProcessor对象
        while (curProcessors < minProcessors) {
            HttpProcessor processor = newProcessor();
            recycle(processor);
        }
    }

    /**
     * 负责创建HttpProcessor实例，并将curProcessors加1
     *
     * @return 返回新创建的HttpProcessor实例
     */
    private HttpProcessor newProcessor() {
        // TODO: 2017/1/6  
        HttpProcessor processor = new HttpProcessor(this, curProcessors++);
        return processor;
    }

    /**
     * 将新创建的HttpProcessor对象压入栈
     *
     * @param processor 新创建的HttpProcessor对象
     */
    void recycle(HttpProcessor processor) {
        processors.push(processor);
    }

    @Override
    public void setContainer(Container container) {

    }

    @Override
    public Container getContainer() {
        return null;
    }

    @Override
    public void createRequest() {

    }

    @Override
    public void createResponse() {

    }

    @Override
    public void setSocketFactory(ServerSocketFactory socketFactory) {
        this.socketFactory = socketFactory;
    }

    @Override
    public ServerSocketFactory getSocketFactory() {
        if (socketFactory == null) {
            synchronized (this) {
                socketFactory = new DefaultServerSocketFactory();
            }
        }
        return socketFactory;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getBacklog() {
        return backlog;
    }

    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }

    public int getMinProcessors() {
        return minProcessors;
    }

    public void setMinProcessors(int minProcessors) {
        this.minProcessors = minProcessors;
    }

    public int getMaxProcessors() {
        return maxProcessors;
    }

    public void setMaxProcessors(int maxProcessors) {
        this.maxProcessors = maxProcessors;
    }

    public int getCurProcessors() {
        return curProcessors;
    }
}
