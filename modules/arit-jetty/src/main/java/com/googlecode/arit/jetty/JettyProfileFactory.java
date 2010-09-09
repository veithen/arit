package com.googlecode.arit.jetty;

import com.googlecode.arit.ServerContext;
import com.googlecode.arit.ServerProfile;
import com.googlecode.arit.ServerProfileFactory;

public class JettyProfileFactory implements ServerProfileFactory {
    public ServerProfile createServerProfile(ServerContext serverContext) {
        if (serverContext.getApplicationClassLoader().getClass().getName().equals("org.mortbay.jetty.webapp.WebAppClassLoader")) {
            return new JettyProfile();
        } else {
            return null;
        }
    }
}
