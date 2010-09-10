package com.googlecode.arit.mbeans;

import java.lang.reflect.Field;

import javax.management.MBeanServer;

import com.googlecode.arit.rbeans.RBeanFactory;
import com.sun.jmx.interceptor.MBeanServerInterceptor;
import com.sun.jmx.mbeanserver.Repository;

public class SunMBeanServerInspector implements MBeanServerInspector {
    private final RBeanFactory<JmxMBeanServerRBean> jmxMBeanServerRBF;
    private final Field repositoryField;
    private final boolean isJava6;
    
    public SunMBeanServerInspector(RBeanFactory<JmxMBeanServerRBean> jmxMBeanServerRBF, Field repositoryField, boolean isJava6) {
        this.jmxMBeanServerRBF = jmxMBeanServerRBF;
        this.repositoryField = repositoryField;
        this.isJava6 = isJava6;
    }

    public MBeanRepository inspect(MBeanServer mbs) {
        try {
            if (jmxMBeanServerRBF.appliesTo(mbs)) {
                MBeanServerInterceptor interceptor = (MBeanServerInterceptor)jmxMBeanServerRBF.createRBean(mbs).getInterceptor();
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
