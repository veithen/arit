package com.googlecode.arit.mbeans;

import com.googlecode.arit.rbeans.RBean;
import com.googlecode.arit.rbeans.SeeAlso;

@RBean(target="com.sun.jmx.interceptor.MBeanServerInterceptor")
@SeeAlso(DefaultMBeanServerInterceptorRBean.class)
public interface MBeanServerInterceptorRBean {

}
