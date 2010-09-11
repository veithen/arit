package com.googlecode.arit.rbeans;

import junit.framework.Assert;

import org.junit.Test;

import com.googlecode.arit.rbeans.test1.DummyClass1;
import com.googlecode.arit.rbeans.test1.DummyClass1RBean;
import com.googlecode.arit.rbeans.test2.Parent;
import com.googlecode.arit.rbeans.test2.ParentRBean;
import com.googlecode.arit.rbeans.test3.Car;
import com.googlecode.arit.rbeans.test3.CarRBean;
import com.googlecode.arit.rbeans.test3.VehicleHolder;
import com.googlecode.arit.rbeans.test3.VehicleHolderRBean;

public class RBeanTest {
    @Test
    public void testPrivateAttributeAccess() throws Exception {
        RBeanFactory rbf = new RBeanFactory(DummyClass1RBean.class);
        DummyClass1RBean rbean = rbf.createRBean(DummyClass1RBean.class, new DummyClass1());
        Assert.assertEquals("somevalue", rbean.getValue());
        Assert.assertEquals("Hello (my value is somevalue)", rbean.sayHello());
    }
    
    @Test
    public void testReturnValueWrapping() throws Exception {
        RBeanFactory rbf = new RBeanFactory(ParentRBean.class);
        ParentRBean rbean = rbf.createRBean(ParentRBean.class, new Parent());
        Assert.assertEquals("Hello", rbean.getChild().sayHello());
    }
    
    @Test
    public void testSeeAlso() throws Exception {
        RBeanFactory rbf = new RBeanFactory(VehicleHolderRBean.class);
        VehicleHolderRBean rbean = rbf.createRBean(VehicleHolderRBean.class, new VehicleHolder(new Car()));
        Assert.assertTrue(rbean.getVehicle() instanceof CarRBean);
    }
}
