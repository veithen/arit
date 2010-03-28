package com.google.code.arit.websphere;

import javax.management.MBeanServer;

import com.ibm.websphere.management.AdminServiceFactory;

public class WASUtil {
    private WASUtil() {}
    
    public static MBeanServer getMBeanServer() {
        return AdminServiceFactory.getMBeanFactory().getMBeanServer();
    }
}
