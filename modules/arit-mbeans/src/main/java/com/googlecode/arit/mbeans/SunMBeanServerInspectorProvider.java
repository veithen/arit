package com.googlecode.arit.mbeans;

import com.google.code.arit.Provider;
import com.google.code.arit.util.ReflectionUtil;
import com.sun.jmx.interceptor.DefaultMBeanServerInterceptor;
import com.sun.jmx.interceptor.MBeanServerInterceptor;
import com.sun.jmx.mbeanserver.JmxMBeanServer;
import com.sun.jmx.mbeanserver.Repository;

public class SunMBeanServerInspectorProvider implements Provider<MBeanServerInspector> {
    public MBeanServerInspector getImplementation() {
        try {
            return new SunMBeanServerInspector(
                    ReflectionUtil.getField(JmxMBeanServer.class, MBeanServerInterceptor.class),
                    ReflectionUtil.getField(DefaultMBeanServerInterceptor.class, Repository.class));
        } catch (NoSuchFieldException ex) {
            return null;
        }
    }
}
