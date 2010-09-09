package com.googlecode.arit.tomcat;

import org.apache.catalina.loader.WebappClassLoader;
import org.apache.naming.resources.ProxyDirContext;

import com.googlecode.arit.ServerProfile;

public class TomcatProfile implements ServerProfile {
    public String identifyApplication(ClassLoader classLoader) {
        if (classLoader instanceof WebappClassLoader) {
            WebappClassLoader wacl = (WebappClassLoader)classLoader;
            ProxyDirContext context = (ProxyDirContext)wacl.getResources();
            // Tomcat removes the DirContext when stopping the application
            return context == null ? "<defunct>" : context.getContextName();
        } else {
            return null;
        }
    }
}
