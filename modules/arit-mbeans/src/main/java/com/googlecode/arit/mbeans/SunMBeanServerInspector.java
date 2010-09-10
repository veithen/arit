package com.googlecode.arit.mbeans;

import javax.management.MBeanServer;

import com.googlecode.arit.rbeans.RBeanFactory;
import com.sun.jmx.mbeanserver.Repository;

public class SunMBeanServerInspector implements MBeanServerInspector {
    private final RBeanFactory rbf;
    private final boolean isJava6;
    
    public SunMBeanServerInspector(RBeanFactory rbf, boolean isJava6) {
        this.rbf = rbf;
        this.isJava6 = isJava6;
    }

    public MBeanRepository inspect(MBeanServer mbs) {
        if (rbf.getRBeanInfo(JmxMBeanServerRBean.class).getTargetClass().isInstance(mbs)) {
            MBeanServerInterceptorRBean interceptor = rbf.createRBean(JmxMBeanServerRBean.class, mbs).getInterceptor();
            Repository repository = (Repository)((DefaultMBeanServerInterceptorRBean)interceptor).getRepository();
            return isJava6 ? new SunJava6MBeanRepository(repository) : new SunJava5MBeanRepository(repository);
        } else {
            return null;
        }
    }
}
