/*
 * Copyright 2010-2011 Andreas Veithen
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

import javax.management.MBeanServer;

import com.googlecode.arit.jmx.MBeanServerProvider;
import com.googlecode.arit.rbeans.RBeanFactory;
import com.googlecode.arit.rbeans.RBeanFactoryException;

public class WASMBeanServerProvider implements MBeanServerProvider {
    public MBeanServer getMBeanServer() {
        // Normally, MBeanServerFactory.findMBeanServer("WebSphere") should be enough
        // to locate the WebSphere MBean server on a correctly configured WAS. However,
        // some people configure the JMX over RMI connector at the JRE level (which
        // is not a supported configuration for WebSphere). In this case, the agentId
        // of the WebSphere MBean server changes.
        RBeanFactory rbf;
        try {
            rbf = new RBeanFactory(AdminServiceFactoryRBean.class);
        } catch (RBeanFactoryException ex) {
            return null;
        }
        return rbf.createRBean(AdminServiceFactoryRBean.class).getMBeanFactory().getMBeanServer();
    }
}
