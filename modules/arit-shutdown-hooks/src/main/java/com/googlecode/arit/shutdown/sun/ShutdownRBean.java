package com.googlecode.arit.shutdown.sun;

import java.util.Collection;

import com.googlecode.arit.rbeans.Accessor;
import com.googlecode.arit.rbeans.RBean;

@RBean(target="java.lang.Shutdown", isStatic=true)
public interface ShutdownRBean {
    @Accessor(name="hooks")
    Collection<?> getHooks();
}
