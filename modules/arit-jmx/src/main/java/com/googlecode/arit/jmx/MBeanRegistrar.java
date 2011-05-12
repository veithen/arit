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
package com.googlecode.arit.jmx;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Disposable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

/**
 * Component that automatically registers the MBeans provided by components with
 * role {@link MBeanProvider}.
 */
@Component(role=MBeanRegistrar.class)
public class MBeanRegistrar implements Initializable, Disposable {
    private MBeanServer mbs;
    private final List<ObjectName> registeredObjectNames = new ArrayList<ObjectName>();
    
    @Requirement(role=MBeanProvider.class)
    private Map<String,MBeanProvider> mbeanProviders;
    
    @Requirement(role=MBeanServerProvider.class)
    private List<MBeanServerProvider> mbeanServerProviders;
    
    public void initialize() throws InitializationException {
        for (MBeanServerProvider mbeanServerProvider : mbeanServerProviders) {
            mbs = mbeanServerProvider.getMBeanServer();
            if (mbs != null) {
                break;
            }
        }
        if (mbs == null) {
            mbs = ManagementFactory.getPlatformMBeanServer();
        }
        for (Map.Entry<String,MBeanProvider> entry : mbeanProviders.entrySet()) {
            try {
                registeredObjectNames.add(entry.getValue().registerMBean(mbs, new ObjectName("com.googlecode.arit:type=" + entry.getKey())).getObjectName());
            } catch (JMException ex) {
                throw new InitializationException("Unable to register MBean", ex);
            }
        }
    }

    public void dispose() {
        for (ObjectName name : registeredObjectNames) {
            try {
                mbs.unregisterMBean(name);
            } catch (JMException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
