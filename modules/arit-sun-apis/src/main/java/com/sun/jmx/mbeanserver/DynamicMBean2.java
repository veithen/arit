package com.sun.jmx.mbeanserver;

import javax.management.MBeanServer;
import javax.management.ObjectName;

public interface DynamicMBean2 extends javax.management.DynamicMBean {
    Object getResource();
    String getClassName();
    void preRegister2(MBeanServer mbs, ObjectName name) throws Exception;
    void registerFailed();
}