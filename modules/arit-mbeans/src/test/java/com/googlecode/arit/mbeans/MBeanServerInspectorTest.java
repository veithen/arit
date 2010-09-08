package com.googlecode.arit.mbeans;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.google.code.arit.ProviderFinder;

import junit.framework.TestCase;

public class MBeanServerInspectorTest extends TestCase {
    public void test() throws Exception {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        Object mbean = new Dummy();
        ObjectName name = new ObjectName("Test:type=Dummy");
        mbs.registerMBean(mbean, name);
        try {
            for (MBeanServerInspector inspector : ProviderFinder.find(MBeanServerInspector.class)) {
                MBeanRepository repository = inspector.inspect(mbs);
                if (repository != null) {
                    assertSame(mbean, repository.retrieve(name));
                }
            }
        } finally {
            mbs.unregisterMBean(name);
        }
    }
}
