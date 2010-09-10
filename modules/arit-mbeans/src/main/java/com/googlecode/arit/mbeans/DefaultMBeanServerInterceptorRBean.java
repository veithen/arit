package com.googlecode.arit.mbeans;

import com.googlecode.arit.rbeans.Accessor;
import com.googlecode.arit.rbeans.RBean;

@RBean(targetClass="com.sun.jmx.interceptor.DefaultMBeanServerInterceptor")
public interface DefaultMBeanServerInterceptorRBean extends MBeanServerInterceptorRBean {
    @Accessor(name="repository")
    Object getRepository();
}
