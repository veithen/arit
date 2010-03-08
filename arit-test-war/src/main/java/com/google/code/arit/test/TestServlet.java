package com.google.code.arit.test;

import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.derby.jdbc.EmbeddedDriver;

public class TestServlet extends HttpServlet {
    private final static ThreadLocal<HttpServlet> threadLocal = new ThreadLocal<HttpServlet>();
    
    @Override
    public void init() throws ServletException {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // Do nothing
            }
        }, 0, 1000);
        new EmbeddedDriver();
        threadLocal.set(this);
    }
}
