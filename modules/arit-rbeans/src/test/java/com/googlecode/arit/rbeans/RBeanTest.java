package com.googlecode.arit.rbeans;

import junit.framework.Assert;

import org.junit.Test;

import com.googlecode.arit.rbeans.test1.DummyClass1;
import com.googlecode.arit.rbeans.test1.DummyClass1RBean;
import com.googlecode.arit.rbeans.test2.Parent;
import com.googlecode.arit.rbeans.test2.ParentRBean;
import com.googlecode.arit.rbeans.test3.Car;
import com.googlecode.arit.rbeans.test3.CarRBean;
import com.googlecode.arit.rbeans.test3.TruckRBean;
import com.googlecode.arit.rbeans.test3.VehicleHolder;
import com.googlecode.arit.rbeans.test3.VehicleHolderRBean;
import com.googlecode.arit.rbeans.test4.Stuff;
import com.googlecode.arit.rbeans.test4.StuffRegistry;
import com.googlecode.arit.rbeans.test4.StuffRegistryRBean;
import com.googlecode.arit.rbeans.test5.MissingAnnotationRBean;
import com.googlecode.arit.rbeans.test5.NonExistingAttributeRBean;
import com.googlecode.arit.rbeans.test5.NonExistingClassRBean;
import com.googlecode.arit.rbeans.test5.NonExistingMethodRBean;
import com.googlecode.arit.rbeans.test6.CyclicSeeAlsoRBean1;
import com.googlecode.arit.rbeans.test6.CyclicSeeAlsoRBean2;

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
    
    @Test
    public void testStaticRBean() throws Exception {
        RBeanFactory rbf = new RBeanFactory(StuffRegistryRBean.class);
        StuffRegistryRBean rbean = rbf.createRBean(StuffRegistryRBean.class);
        Stuff stuff = new Stuff();
        StuffRegistry.registerStuff(stuff);
        Assert.assertSame(stuff, rbean.getRegisteredStuff().get(0));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testCreateRBeanWithInvalidArguments1() throws Exception {
        RBeanFactory rbf = new RBeanFactory(TruckRBean.class);
        // TruckRBean is not a static RBean -> should throw exception
        rbf.createRBean(TruckRBean.class);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testCreateRBeanWithInvalidArguments2() throws Exception {
        RBeanFactory rbf = new RBeanFactory(StuffRegistryRBean.class);
        // StuffRegistryRBean is a static RBean -> should throw exception
        rbf.createRBean(StuffRegistryRBean.class, new StuffRegistry());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testGetRBeanInfoWithInvalidArgument() throws Exception {
        RBeanFactory rbf = new RBeanFactory(TruckRBean.class);
        rbf.createRBean(StuffRegistryRBean.class);
    }
    
    @Test(expected=TargetClassNotFoundException.class)
    public void testNonExistingTargetClass() throws Exception {
        new RBeanFactory(NonExistingClassRBean.class);
    }
    
    @Test(expected=TargetMemberNotFoundException.class)
    public void testNonExistingTargetMethod() throws Exception {
        new RBeanFactory(NonExistingMethodRBean.class);
    }
    
    @Test(expected=TargetMemberNotFoundException.class)
    public void testNonExistingTargetAttribute() throws Exception {
        new RBeanFactory(NonExistingAttributeRBean.class);
    }
    
    @Test
    public void testCyclicSeeAlso() throws Exception {
        RBeanFactory rbf = new RBeanFactory(CyclicSeeAlsoRBean1.class);
        // This would throw an exception if the factory didn't interpret the @SeeAlso annotation
        rbf.getRBeanInfo(CyclicSeeAlsoRBean2.class);
    }
    
    @Test(expected=MissingRBeanAnnotationException.class)
    public void testMissingRBeanAnnotation() throws Exception {
        new RBeanFactory(MissingAnnotationRBean.class);
    }
}
