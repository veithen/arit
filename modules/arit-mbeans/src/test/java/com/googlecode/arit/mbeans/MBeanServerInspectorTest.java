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
package com.googlecode.arit.mbeans;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.modelmbean.ModelMBeanInfoSupport;
import javax.management.modelmbean.RequiredModelMBean;

import org.codehaus.plexus.PlexusTestCase;

public class MBeanServerInspectorTest extends PlexusTestCase {
    public void testDynamicMBean() throws Exception {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        Object mbean = new Dummy();
        ObjectName name = new ObjectName("Test:type=Dummy");
        mbs.registerMBean(mbean, name);
        boolean found = false;
        try {
            for (MBeanServerInspectorPlugin inspector : getContainer().lookupList(MBeanServerInspectorPlugin.class)) {
                if (inspector.isAvailable()) {
                    MBeanAccessor accessor = inspector.inspect(mbs);
                    if (accessor != null) {
                        assertSame(mbean, accessor.retrieve(name));
                        found = true;
                    }
                }
            }
        } finally {
            mbs.unregisterMBean(name);
        }
        assertTrue(found);
    }
    
    public void testRequiredModelMBean() throws Exception {
        Object resource = new Dummy();
        RequiredModelMBean mbean = new RequiredModelMBean();
        mbean.setManagedResource(resource, "ObjectReference");
        mbean.setModelMBeanInfo(new ModelMBeanInfoSupport(Dummy.class.getName(), "Dummy", null, null, null, null));
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = new ObjectName("Test:type=Dummy");
        mbs.registerMBean(mbean, name);
        boolean found = false;
        try {
            for (MBeanServerInspectorPlugin inspector : getContainer().lookupList(MBeanServerInspectorPlugin.class)) {
                if (inspector.isAvailable()) {
                    MBeanAccessor accessor = inspector.inspect(mbs);
                    if (accessor != null) {
                        assertSame(resource, accessor.retrieve(name));
                        found = true;
                    }
                }
            }
        } finally {
            mbs.unregisterMBean(name);
        }
        assertTrue(found);
    }
}
