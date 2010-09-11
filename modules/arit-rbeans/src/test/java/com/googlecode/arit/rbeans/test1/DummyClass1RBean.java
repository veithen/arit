package com.googlecode.arit.rbeans.test1;

import com.googlecode.arit.rbeans.Accessor;
import com.googlecode.arit.rbeans.RBean;

@RBean(target="com.googlecode.arit.rbeans.test1.DummyClass1")
public interface DummyClass1RBean {
    @Accessor(name="value")
    String getValue();
    
    String sayHello();
}
