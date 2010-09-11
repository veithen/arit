package com.googlecode.arit.rbeans.test2;

import com.googlecode.arit.rbeans.RBean;

@RBean(target="com.googlecode.arit.rbeans.test2.Child")
public interface ChildRBean {
    String sayHello();
}
