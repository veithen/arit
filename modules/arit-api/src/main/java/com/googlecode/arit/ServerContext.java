package com.googlecode.arit;

import javax.servlet.ServletContext;

public class ServerContext {
    private final ServletContext servletContext;
    private final ClassLoader applicationClassLoader;
    
    public ServerContext(ServletContext servletContext, ClassLoader applicationClassLoader) {
        this.servletContext = servletContext;
        this.applicationClassLoader = applicationClassLoader;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public ClassLoader getApplicationClassLoader() {
        return applicationClassLoader;
    }
}
