package com.bapocalypse.Jerrymouse;

import javax.servlet.*;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @package: com.bapocalypse.Jerrymouse
 * @Author: 陈淼
 * @Date: 2016/12/16
 * @Description: 测试所用servlet
 */
public class PrimitiveServlet implements Servlet {
    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        System.out.println("init");
    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        System.out.println("from service");
        PrintWriter out = servletResponse.getWriter();
        out.println("Hello Roses are red");
        out.print("Blue");
    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {
        System.out.println("destroy");
    }
}
