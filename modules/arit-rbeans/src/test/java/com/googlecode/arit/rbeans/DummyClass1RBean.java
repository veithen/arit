package com.googlecode.arit.rbeans;

@RBean(targetClass="com.googlecode.arit.rbeans.DummyClass1")
public interface DummyClass1RBean {
    @Accessor(name="value")
    String getValue();
    
    String sayHello();
}
