package com.google.code.rex.jetty;

import com.google.code.rex.ServerContext;
import com.google.code.rex.ServerProfile;
import com.google.code.rex.ServerProfileFactory;

public class JettyProfileFactory implements ServerProfileFactory {
    public ServerProfile createServerProfile(ServerContext serverContext) {
        if (serverContext.getApplicationClassLoader().getClass().getName().equals("org.mortbay.jetty.webapp.WebAppClassLoader")) {
            return new JettyProfile();
        } else {
            return null;
        }
    }
}
