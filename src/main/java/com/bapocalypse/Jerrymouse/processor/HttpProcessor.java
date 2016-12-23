package com.bapocalypse.Jerrymouse.processor;

import com.bapocalypse.Jerrymouse.connector.http.SocketInputStream;
import com.bapocalypse.Jerrymouse.request.HttpRequest;
import com.bapocalypse.Jerrymouse.request.Request;
import com.bapocalypse.Jerrymouse.response.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @package: com.bapocalypse.Jerrymouse.processor
 * @Author: 陈淼
 * @Date: 2016/12/21
 * @Description:
 */
public class HttpProcessor {

    public void process(Socket socket) {
        OutputStream outputStream = null;
        InputStream inputStream = null; //todo
        try {
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
            Request request = new Request(inputStream); //todo
            Response response = new Response(outputStream);//todo
            response.setRequest(request);
            //todo
            if (request.getUri().startsWith("/servlet/")) {
                ServletProcessor processor = new ServletProcessor();
                processor.process(request, response);
            } else {
                StaticResourceProcessor processor = new StaticResourceProcessor();
                processor.process(request, response);
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
