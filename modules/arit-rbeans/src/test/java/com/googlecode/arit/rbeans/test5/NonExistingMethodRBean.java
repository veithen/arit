package com.googlecode.arit.rbeans.test5;

import com.googlecode.arit.rbeans.RBean;

@RBean(target="java.lang.String")
public interface NonExistingMethodRBean {
    void nonExistingMethod();
}
