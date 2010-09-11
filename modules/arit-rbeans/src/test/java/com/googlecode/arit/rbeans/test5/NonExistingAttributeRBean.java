package com.googlecode.arit.rbeans.test5;

import com.googlecode.arit.rbeans.Accessor;
import com.googlecode.arit.rbeans.RBean;

@RBean(target="java.lang.String")
public interface NonExistingAttributeRBean {
    @Accessor(name="nonExistingAttribute")
    String getNonExistingAttribute();
}
