package com.google.code.arit.tomcat;

import org.apache.catalina.loader.WebappClassLoader;
import org.apache.naming.resources.ProxyDirContext;

import com.google.code.arit.ServerProfile;

public class TomcatProfile implements ServerProfile {
    public String identifyApplication(ClassLoader classLoader) {
        if (classLoader instanceof WebappClassLoader) {
            WebappClassLoader wacl = (WebappClassLoader)classLoader;
            return ((ProxyDirContext)wacl.getResources()).getContextName();
        } else {
            return null;
        }
    }
}
