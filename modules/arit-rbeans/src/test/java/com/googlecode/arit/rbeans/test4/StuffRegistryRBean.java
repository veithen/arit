package com.googlecode.arit.rbeans.test4;

import java.util.List;

import com.googlecode.arit.rbeans.Accessor;
import com.googlecode.arit.rbeans.RBean;

@RBean(target="com.googlecode.arit.rbeans.test4.StuffRegistry", isStatic=true)
public interface StuffRegistryRBean {
    @Accessor(name="registeredStuff")
    List<Stuff> getRegisteredStuff();
}
