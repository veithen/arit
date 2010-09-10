package com.googlecode.arit.rbeans;

@RBean(target="com.googlecode.arit.rbeans.Parent")
public interface ParentRBean {
    ChildRBean getChild();
}
