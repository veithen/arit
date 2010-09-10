package com.googlecode.arit.rbeans;

@RBean(targetClass="com.googlecode.arit.rbeans.Parent")
public interface ParentRBean {
    ChildRBean getChild();
}
