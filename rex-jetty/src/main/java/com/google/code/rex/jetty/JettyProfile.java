package com.google.code.rex.jetty;

import org.mortbay.jetty.webapp.WebAppClassLoader;
import org.mortbay.jetty.webapp.WebAppContext;

import com.google.code.rex.ServerProfile;

public class JettyProfile implements ServerProfile {
    public String identifyApplication(ClassLoader classLoader) {
        if (classLoader instanceof WebAppClassLoader) {
            WebAppClassLoader wacl = (WebAppClassLoader)classLoader;
            WebAppContext context = (WebAppContext)wacl.getContext();
            return context.getContextPath();
        } else {
            return null;
        }
    }
}
