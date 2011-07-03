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
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Component that automatically registers the MBeans provided by components with
 * role {@link MBeanProvider}.
 */
public class MBeanRegistrar implements InitializingBean, DisposableBean {
    private MBeanServer mbs;
    private final List<ObjectName> registeredObjectNames = new ArrayList<ObjectName>();
    
    @Autowired
    private Map<String,MBeanProvider> mbeanProviders;
    
    @Autowired
    private Set<MBeanServerProvider> mbeanServerProviders;
    
    public void afterPropertiesSet() throws Exception {
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
            registeredObjectNames.add(entry.getValue().registerMBean(mbs, new ObjectName("com.googlecode.arit:type=" + entry.getKey())).getObjectName());
        }
    }

    public void destroy() throws Exception {
        for (ObjectName name : registeredObjectNames) {
            mbs.unregisterMBean(name);
        }
    }
}
