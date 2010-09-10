package com.googlecode.arit.mbeans;

import java.lang.reflect.Field;

import javax.management.MBeanServer;

import com.googlecode.arit.rbeans.RBeanFactory;
import com.sun.jmx.interceptor.MBeanServerInterceptor;
import com.sun.jmx.mbeanserver.Repository;

public class SunMBeanServerInspector implements MBeanServerInspector {
    private final RBeanFactory rbf;
    private final Field repositoryField;
    private final boolean isJava6;
    
    public SunMBeanServerInspector(RBeanFactory rbf, Field repositoryField, boolean isJava6) {
        this.rbf = rbf;
        this.repositoryField = repositoryField;
        this.isJava6 = isJava6;
    }

    public MBeanRepository inspect(MBeanServer mbs) {
        try {
            if (rbf.getRBeanInfo(JmxMBeanServerRBean.class).getTargetClass().isInstance(mbs)) {
                MBeanServerInterceptor interceptor = (MBeanServerInterceptor)rbf.createRBean(JmxMBeanServerRBean.class, mbs).getInterceptor();
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
