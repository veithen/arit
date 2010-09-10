package com.googlecode.arit.websphere;

import com.googlecode.arit.rbeans.Accessor;
import com.googlecode.arit.rbeans.RBean;

@RBean(targetClass="com.ibm.ws.classloader.CompoundClassLoader")
public interface CompoundClassLoader {
    @Accessor(name="name")
    String getName();
}
