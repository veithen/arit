package com.google.code.arit.tomcat;

import com.google.code.arit.ServerContext;
import com.google.code.arit.ServerProfile;
import com.google.code.arit.ServerProfileFactory;

public class TomcatProfileFactory implements ServerProfileFactory {
    public ServerProfile createServerProfile(ServerContext serverContext) {
        if (serverContext.getApplicationClassLoader().getClass().getName().equals("org.apache.catalina.loader.WebappClassLoader")) {
            return new TomcatProfile();
        } else {
            return null;
        }
    }
}
