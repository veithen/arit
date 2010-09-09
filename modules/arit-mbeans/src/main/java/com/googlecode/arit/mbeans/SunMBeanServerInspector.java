package com.googlecode.arit.mbeans;

import java.lang.reflect.Field;

import javax.management.MBeanServer;

import com.sun.jmx.interceptor.MBeanServerInterceptor;
import com.sun.jmx.mbeanserver.JmxMBeanServer;
import com.sun.jmx.mbeanserver.Repository;

public class SunMBeanServerInspector implements MBeanServerInspector {
    private final Field interceptorField;
    private final Field repositoryField;
    private final boolean isJava6;
    
    public SunMBeanServerInspector(Field interceptorField, Field repositoryField, boolean isJava6) {
        this.interceptorField = interceptorField;
        this.repositoryField = repositoryField;
        this.isJava6 = isJava6;
    }

    public MBeanRepository inspect(MBeanServer mbs) {
        try {
            if (mbs instanceof JmxMBeanServer) {
                MBeanServerInterceptor interceptor = (MBeanServerInterceptor)interceptorField.get(mbs);
                Repository repository = (Repository)repositoryField.get(interceptor);
                return isJava6 ? new SunJava6MBeanRepository(repository) : new SunJava5MBeanRepository(repository);
            } else {
                return null;
            }
        } catch (IllegalAccessException ex) {
            throw new IllegalAccessError(ex.getMessage());
        }
    }
}
