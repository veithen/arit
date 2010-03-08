package com.google.code.arit.jdbc;

import java.util.List;

import org.apache.derby.jdbc.AutoloadedDriver;
import org.apache.derby.jdbc.EmbeddedDriver;
import org.junit.Assert;
import org.junit.Test;

import com.google.code.arit.ProviderFinder;
import com.google.code.arit.jdbc.DriverManagerInspector;

public class DriverManagerInspectorTest {
    @Test
    public void test() {
        List<DriverManagerInspector> inspectors = ProviderFinder.find(DriverManagerInspector.class);
        Assert.assertEquals(1, inspectors.size());
        DriverManagerInspector inspector = inspectors.get(0);
        new EmbeddedDriver();
        List<Class<?>> classes = inspector.getDriverClasses();
        Assert.assertTrue(classes.contains(AutoloadedDriver.class));
    }
}
