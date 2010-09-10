package com.googlecode.arit.mbeans;

import com.googlecode.arit.Provider;
import com.googlecode.arit.rbeans.RBeanFactory;
import com.googlecode.arit.rbeans.RBeanFactoryException;

public class SunMBeanServerInspectorProvider implements Provider<MBeanServerInspector> {
    public MBeanServerInspector getImplementation() {
        try {
            return new SunMBeanServerInspector(new RBeanFactory(JmxMBeanServerRBean.class),
                    !System.getProperty("java.version").startsWith("1.5"));
        } catch (RBeanFactoryException ex) {
            return null;
        }
    }
}
