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
    private Stack<HttpProcessor> processors = new Stack<>();  //用于存储HttpProcessor实例，即对象池
    private boolean stopped = false;                          //钩子，用于停止循环

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
     * 不断接收客户端的请求，并为每一个请求创建HttpProcessor对象，
     * 称HttpConnector实例中run()方法运行时所在的线程为“连接器线程”。
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
            //得到一个HttpProcessor实例，若createProcessor返回null，服务器会简单关闭套接字，
            // 不对这个引入的HTTP请求进行处理
            HttpProcessor processor = createProcessor();
            if (processor == null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                continue;
            }
            // TODO: 2017/1/7
            processor.process(socket);
        }
    }

    /**
     * 启动后台处理守护线程
     */
    private void threadStart() {
        Thread thread = new Thread(this);
        //设为守护线程后，一旦后台没有除该线程以外的线程运行，立马停止处理
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
    @Override
    public void recycle(HttpProcessor processor) {
        processors.push(processor);
    }

    /**
     * 大多数时间里，此方法并不会创建一个新的HttpProcessor实例，而是从池中获取一个对象，
     * 如果栈中还有HttpProcessor实例可以使用，就从栈中弹出一个HttpProcessor实例，将其返回。
     * 如果栈已经空了，且已经创建的HttpProcessor实例的数量还没有超过限定的最大值，
     * createProcessor()就会新建一个HttpProcessor实例。若已经创建的实例数量已经达到最大限定值，
     * 则返回null，此时，服务器会简单地关闭套接字，不再对新的请求进行处理。
     *
     * @return 返回一个HttpProcessor实例或null
     */
    private HttpProcessor createProcessor() {
        synchronized (processors) {
            if (processors.size() > 0) {
                return processors.pop();
            }
            if (maxProcessors > 0 && curProcessors < maxProcessors) {
                return newProcessor();
            } else {
                return null;
            }
        }
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
