package com.google.code.arit.jetty;

import com.google.code.arit.ServerContext;
import com.google.code.arit.ServerProfile;
import com.google.code.arit.ServerProfileFactory;

public class JettyProfileFactory implements ServerProfileFactory {
    public ServerProfile createServerProfile(ServerContext serverContext) {
        if (serverContext.getApplicationClassLoader().getClass().getName().equals("org.mortbay.jetty.webapp.WebAppClassLoader")) {
            return new JettyProfile();
        } else {
            return null;
        }
    }
}
