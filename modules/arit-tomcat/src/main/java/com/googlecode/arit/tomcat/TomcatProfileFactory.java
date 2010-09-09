package com.googlecode.arit.tomcat;

import org.codehaus.plexus.component.annotations.Component;

import com.googlecode.arit.ServerContext;
import com.googlecode.arit.ServerProfile;
import com.googlecode.arit.ServerProfileFactory;

@Component(role=ServerProfileFactory.class, hint="tomcat")
public class TomcatProfileFactory implements ServerProfileFactory {
    public ServerProfile createServerProfile(ServerContext serverContext) {
        if (serverContext.getApplicationClassLoader().getClass().getName().equals("org.apache.catalina.loader.WebappClassLoader")) {
            return new TomcatProfile();
        } else {
            return null;
        }
    }
}
