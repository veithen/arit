package com.googlecode.arit.websphere;

import com.googlecode.arit.ServerProfile;
import com.googlecode.arit.rbeans.RBeanFactory;

public class WASProfile implements ServerProfile {
    private final RBeanFactory<CompoundClassLoaderRBean> compoundClassLoaderRBF;
    
    public WASProfile(RBeanFactory<CompoundClassLoaderRBean> compoundClassLoaderRBF) {
        this.compoundClassLoaderRBF = compoundClassLoaderRBF;
    }

    public String identifyApplication(ClassLoader classLoader) {
        if (compoundClassLoaderRBF.appliesTo(classLoader)) {
            return compoundClassLoaderRBF.createRBean(classLoader).getName();
        } else {
            return null;
        }
    }
}
