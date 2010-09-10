package com.googlecode.arit.mbeans;

import com.googlecode.arit.Provider;
import com.googlecode.arit.rbeans.RBeanFactory;
import com.googlecode.arit.util.ReflectionUtil;
import com.sun.jmx.interceptor.DefaultMBeanServerInterceptor;
import com.sun.jmx.mbeanserver.Repository;

public class SunMBeanServerInspectorProvider implements Provider<MBeanServerInspector> {
    public MBeanServerInspector getImplementation() {
        RBeanFactory rbf = new RBeanFactory();
        if (rbf.check(JmxMBeanServerRBean.class)) {
            try {
                return new SunMBeanServerInspector(rbf,
                        ReflectionUtil.getField(DefaultMBeanServerInterceptor.class, Repository.class),
                        !System.getProperty("java.version").startsWith("1.5"));
            } catch (NoSuchFieldException ex) {
                return null;
            }
        } else {
            return null;
        }
    }
}
