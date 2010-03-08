package com.google.code.arit.jdbc;

import java.sql.DriverManager;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.google.code.arit.ProviderFinder;

public class DriverManagerInspectorTest {
    @Test
    public void test() throws Exception {
        List<DriverManagerInspector> inspectors = ProviderFinder.find(DriverManagerInspector.class);
        Assert.assertEquals(1, inspectors.size());
        DriverManagerInspector inspector = inspectors.get(0);
        MyDriver driver = new MyDriver();
        DriverManager.registerDriver(driver);
        List<Class<?>> classes = inspector.getDriverClasses();
        Assert.assertTrue(classes.contains(MyDriver.class));
        DriverManager.deregisterDriver(driver);
    }
}
