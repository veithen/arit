package com.google.code.arit.tomcat;

import org.apache.catalina.loader.WebappClassLoader;

import com.google.code.arit.ServerProfile;

public class TomcatProfile implements ServerProfile {
    public String identifyApplication(ClassLoader classLoader) {
        if (classLoader instanceof WebappClassLoader) {
            WebappClassLoader wacl = (WebappClassLoader)classLoader;
            // TODO: find a way to get the application name
            return wacl.toString();
        } else {
            return null;
        }
    }
}
