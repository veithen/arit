package com.googlecode.arit.websphere;

import com.googlecode.arit.rbeans.Accessor;
import com.googlecode.arit.rbeans.RBean;

@RBean(target="com.ibm.ws.classloader.CompoundClassLoader")
public interface CompoundClassLoaderRBean {
    @Accessor(name="name")
    String getName();
}
