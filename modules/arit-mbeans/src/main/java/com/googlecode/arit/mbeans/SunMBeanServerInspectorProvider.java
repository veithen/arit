package com.googlecode.arit.mbeans;

import com.googlecode.arit.Provider;
import com.googlecode.arit.rbeans.RBeanFactory;
import com.googlecode.arit.rbeans.RBeanFactoryException;
import com.googlecode.arit.util.ReflectionUtil;
import com.sun.jmx.interceptor.DefaultMBeanServerInterceptor;
import com.sun.jmx.mbeanserver.Repository;

public class SunMBeanServerInspectorProvider implements Provider<MBeanServerInspector> {
    public MBeanServerInspector getImplementation() {
        try {
            return new SunMBeanServerInspector(new RBeanFactory(JmxMBeanServerRBean.class),
                    ReflectionUtil.getField(DefaultMBeanServerInterceptor.class, Repository.class),
                    !System.getProperty("java.version").startsWith("1.5"));
        } catch (NoSuchFieldException ex) {
            return null;
        } catch (RBeanFactoryException ex) {
            return null;
        }
    }
}
