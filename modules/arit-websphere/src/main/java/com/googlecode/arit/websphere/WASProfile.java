package com.googlecode.arit.websphere;

import com.googlecode.arit.ServerProfile;
import com.googlecode.arit.rbeans.RBeanFactory;

public class WASProfile implements ServerProfile {
    private final RBeanFactory<CompoundClassLoader> compoundClassLoaderRBeanFactory;
    
    public WASProfile(RBeanFactory<CompoundClassLoader> compoundClassLoaderRBeanFactory) {
        this.compoundClassLoaderRBeanFactory = compoundClassLoaderRBeanFactory;
    }

    public String identifyApplication(ClassLoader classLoader) {
        if (compoundClassLoaderRBeanFactory.appliesTo(classLoader)) {
            return compoundClassLoaderRBeanFactory.createRBean(classLoader).getName();
        } else {
            return null;
        }
    }
}
