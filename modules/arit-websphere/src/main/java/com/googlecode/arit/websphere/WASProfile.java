package com.googlecode.arit.websphere;

import com.googlecode.arit.ServerProfile;
import com.googlecode.arit.rbeans.RBeanFactory;

public class WASProfile implements ServerProfile {
    private final RBeanFactory rbf;
    
    public WASProfile(RBeanFactory rbf) {
        this.rbf = rbf;
    }

    public String identifyApplication(ClassLoader classLoader) {
        if (rbf.getRBeanInfo(CompoundClassLoaderRBean.class).getTargetClass().isInstance(classLoader)) {
            return rbf.createRBean(CompoundClassLoaderRBean.class, classLoader).getName();
        } else {
            return null;
        }
    }
}
