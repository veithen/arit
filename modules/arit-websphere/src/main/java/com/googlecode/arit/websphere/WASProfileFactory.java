/*
 * Copyright 2010 Andreas Veithen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
        
        RBeanFactory rbf;
        try {
            rbf = new RBeanFactory(CompoundClassLoaderRBean.class);
        } catch (RBeanFactoryException ex) {
            return null;
        }
        if (rbf.getRBeanInfo(CompoundClassLoaderRBean.class).getTargetClass().isInstance(serverContext.getApplicationClassLoader())) {
            return new WASProfile(rbf);
        } else {
            return null;
        }
    }
}
