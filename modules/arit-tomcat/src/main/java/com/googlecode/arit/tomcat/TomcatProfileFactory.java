package com.googlecode.arit.tomcat;

import com.googlecode.arit.ServerContext;
import com.googlecode.arit.ServerProfile;
import com.googlecode.arit.ServerProfileFactory;

public class TomcatProfileFactory implements ServerProfileFactory {
    public ServerProfile createServerProfile(ServerContext serverContext) {
        if (serverContext.getApplicationClassLoader().getClass().getName().equals("org.apache.catalina.loader.WebappClassLoader")) {
            return new TomcatProfile();
        } else {
            return null;
        }
    }
}
