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

import javax.management.DynamicMBean;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.StandardMBean;
import javax.management.modelmbean.ModelMBeanInfoSupport;
import javax.management.modelmbean.RequiredModelMBean;

import org.apache.log4j.Logger;
import org.apache.log4j.jmx.LoggerDynamicMBean;
import org.codehaus.plexus.PlexusTestCase;

public class MBeanServerInspectorTest extends PlexusTestCase {
    private MBeanAccessor getAccessor(MBeanServer mbs) throws Exception {
        MBeanServerInspector inspector = lookup(MBeanServerInspector.class);
        assertTrue(inspector.isAvailable());
        MBeanAccessor accessor = inspector.inspect(mbs);
        assertNotNull(accessor);
        return accessor;
    }
    
    public void testStandardMBean() throws Exception {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        Dummy impl = new Dummy();
        ObjectName name = mbs.registerMBean(impl, new ObjectName("Test:type=Dummy")).getObjectName();
        try {
            assertSame(impl, getAccessor(mbs).retrieve(name));
        } finally {
            mbs.unregisterMBean(name);
        }
    }
    
    // TODO: not working yet
    public void _testStandardMBean2() throws Exception {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        Dummy impl = new Dummy();
        StandardMBean mbean = new StandardMBean(impl, DummyMBean.class);
        ObjectName name = mbs.registerMBean(mbean, new ObjectName("Test:type=Dummy")).getObjectName();
        try {
            assertSame(impl, getAccessor(mbs).retrieve(name));
        } finally {
            mbs.unregisterMBean(name);
        }
    }
    
    public void testRequiredModelMBean() throws Exception {
        MBeanServerInspector inspector = lookup(MBeanServerInspector.class);
        assertTrue(inspector.isAvailable());
        Dummy impl = new Dummy();
        RequiredModelMBean mbean = new RequiredModelMBean();
        mbean.setManagedResource(impl, "ObjectReference");
        mbean.setModelMBeanInfo(new ModelMBeanInfoSupport(Dummy.class.getName(), "Dummy", null, null, null, null));
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = mbs.registerMBean(mbean, new ObjectName("Test:type=Dummy")).getObjectName();
        try {
            assertSame(impl, getAccessor(mbs).retrieve(name));
        } finally {
            mbs.unregisterMBean(name);
        }
    }
    
    public void testDynamicMBean() throws Exception {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        DynamicMBean mbean = new LoggerDynamicMBean(Logger.getLogger("test"));
        ObjectName name = mbs.registerMBean(mbean, new ObjectName("Test:name=test")).getObjectName();
        try {
            assertSame(mbean, getAccessor(mbs).retrieve(name));
        } finally {
            mbs.unregisterMBean(name);
        }
    }
}
