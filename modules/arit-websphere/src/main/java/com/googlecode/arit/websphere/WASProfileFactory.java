package com.googlecode.arit.websphere;

import java.util.Set;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import org.codehaus.plexus.component.annotations.Component;

import com.googlecode.arit.ServerContext;
import com.googlecode.arit.ServerProfile;
import com.googlecode.arit.ServerProfileFactory;
import com.googlecode.arit.rbeans.RBeanFactory;
import com.googlecode.arit.rbeans.RBeanFactoryException;

@Component(role=ServerProfileFactory.class, hint="websphere")
public class WASProfileFactory implements ServerProfileFactory {
    public ServerProfile createServerProfile(ServerContext serverContext) {
        try {
            serverContext.getApplicationClassLoader().loadClass("com.ibm.websphere.management.AdminServiceFactory");
        } catch (ClassNotFoundException ex) {
            return null;
        }
        MBeanServer mbeanServer = WASUtil.getMBeanServer();
        try {
            Set<ObjectInstance> mbeans = mbeanServer.queryMBeans(new ObjectName("WebSphere:type=Server,*"), null);
            if (mbeans.size() != 1) {
                return null;
            }
            ObjectInstance server = mbeans.iterator().next();
            System.out.println(server.getObjectName());
            System.out.println(server.getClass());
        } catch (MalformedObjectNameException ex) {
            return null;
        }
        
        RBeanFactory<CompoundClassLoader> rbeanFactory;
        try {
            rbeanFactory = RBeanFactory.create(CompoundClassLoader.class);
        } catch (RBeanFactoryException ex) {
            return null;
        }
        if (rbeanFactory.appliesTo(serverContext.getApplicationClassLoader())) {
            return new WASProfile(rbeanFactory);
        } else {
            return null;
        }
    }
}
