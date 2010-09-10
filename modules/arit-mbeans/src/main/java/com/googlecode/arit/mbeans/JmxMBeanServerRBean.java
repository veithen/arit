package com.googlecode.arit.mbeans;

import com.googlecode.arit.rbeans.Accessor;
import com.googlecode.arit.rbeans.RBean;

@RBean(targetClass="com.sun.jmx.mbeanserver.JmxMBeanServer")
public interface JmxMBeanServerRBean {
    @Accessor(name="mbsInterceptor")
    MBeanServerInterceptorRBean getInterceptor();
}
