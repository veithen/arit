package com.googlecode.arit.mbeans;

import java.lang.reflect.Field;

import javax.management.MBeanServer;

import com.sun.jmx.interceptor.MBeanServerInterceptor;
import com.sun.jmx.mbeanserver.JmxMBeanServer;
import com.sun.jmx.mbeanserver.Repository;

public class SunMBeanServerInspector implements MBeanServerInspector {
    private final Field interceptorField;
    private final Field repositoryField;
    
    public SunMBeanServerInspector(Field interceptorField, Field repositoryField) {
        this.interceptorField = interceptorField;
        this.repositoryField = repositoryField;
    }

    public MBeanRepository inspect(MBeanServer mbs) {
        try {
            if (mbs instanceof JmxMBeanServer) {
                MBeanServerInterceptor interceptor = (MBeanServerInterceptor)interceptorField.get(mbs);
                return new SunMBeanRepository((Repository)repositoryField.get(interceptor));
            } else {
                return null;
            }
        } catch (IllegalAccessException ex) {
            throw new IllegalAccessError(ex.getMessage());
        }
    }
}
