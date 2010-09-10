package com.googlecode.arit.rbeans;

import junit.framework.Assert;

import org.junit.Test;

public class RBeanTest {
    @Test
    public void test() throws Exception {
        DummyClass1RBean rbean = RBeanFactory.create(DummyClass1RBean.class).createRBean(new DummyClass1());
        Assert.assertEquals("somevalue", rbean.getValue());
        Assert.assertEquals("Hello (my value is somevalue)", rbean.sayHello());
    }
}
