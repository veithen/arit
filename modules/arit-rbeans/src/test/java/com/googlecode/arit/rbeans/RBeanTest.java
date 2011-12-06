/*
 * Copyright 2010-2011 Andreas Veithen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.arit.rbeans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

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
import com.googlecode.arit.rbeans.test7.Driver;
import com.googlecode.arit.rbeans.test7.DriverManager;
import com.googlecode.arit.rbeans.test7.DriverManagerRBean;
import com.googlecode.arit.rbeans.test7.DriverRBean;
import com.googlecode.arit.rbeans.test8.IncompatibleAttributeTypeRBean;

public class RBeanTest {
    @Test
    public void testPrivateAttributeAccess() throws Exception {
        RBeanFactory rbf = new RBeanFactory(DummyClass1RBean.class);
        DummyClass1 target = new DummyClass1();
        DummyClass1RBean rbean = rbf.createRBean(DummyClass1RBean.class, target);
        assertEquals("somevalue", rbean.getValue());
        assertEquals("Hello (my value is somevalue)", rbean.sayHello());
        assertSame(target, rbean._getTargetObject());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testCreateRBeanWithIncorrectType() throws Exception {
        RBeanFactory rbf = new RBeanFactory(DummyClass1RBean.class);
        rbf.createRBean(DummyClass1RBean.class, new Object());
    }
    
    @Test
    public void testReturnValueWrapping() throws Exception {
        RBeanFactory rbf = new RBeanFactory(ParentRBean.class);
        ParentRBean rbean = rbf.createRBean(ParentRBean.class, new Parent());
        assertEquals("Hello", rbean.getChild().sayHello());
    }
    
    @Test
    public void testSeeAlso() throws Exception {
        RBeanFactory rbf = new RBeanFactory(VehicleHolderRBean.class);
        VehicleHolderRBean rbean = rbf.createRBean(VehicleHolderRBean.class, new VehicleHolder(new Car()));
        assertTrue(rbean.getVehicle() instanceof CarRBean);
    }
    
    @Test
    public void testStaticRBean() throws Exception {
        RBeanFactory rbf = new RBeanFactory(StuffRegistryRBean.class);
        StuffRegistryRBean rbean = rbf.createRBean(StuffRegistryRBean.class);
        Stuff stuff = new Stuff();
        StuffRegistry.registerStuff(stuff);
        assertSame(stuff, rbean.getRegisteredStuff().get(0));
        assertSame(StuffRegistry.class, rbean._getTargetClass());
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
    
    @Test
    public void testWithCollectionReturn() throws Exception {
        RBeanFactory rbf = new RBeanFactory(DriverManagerRBean.class);
        DriverManager driverManager = new DriverManager();
        driverManager.registerDriver(new Driver("test"));
        DriverManagerRBean driverManagerRBean = rbf.createRBean(DriverManagerRBean.class, driverManager);
        DriverRBean driverRBean = driverManagerRBean.getDrivers().iterator().next();
        assertEquals("test", driverRBean.getName());
    }
    
    @Test(expected=TargetMemberNotFoundException.class)
    public void testIncompatibleAttributeType() throws Exception {
        new RBeanFactory(IncompatibleAttributeTypeRBean.class);
    }
}
