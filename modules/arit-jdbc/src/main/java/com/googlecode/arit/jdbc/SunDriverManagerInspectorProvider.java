package com.googlecode.arit.jdbc;

import java.lang.reflect.Field;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.googlecode.arit.Provider;
import com.googlecode.arit.rbeans.RBeanFactory;
import com.googlecode.arit.rbeans.RBeanFactoryException;
import com.googlecode.arit.util.ReflectionUtil;

public class SunDriverManagerInspectorProvider implements Provider<DriverManagerInspector> {
    public DriverManagerInspector getImplementation() {
        try {
            RBeanFactory rbf = new RBeanFactory(SunDriverManagerRBean.class);
            final SunDriverManagerRBean driverManager = rbf.createRBean(SunDriverManagerRBean.class);
            final Field driverClassField = ReflectionUtil.getField(Class.forName("java.sql.DriverInfo"), "driverClass");
            return new DriverManagerInspector() {
                public List<Class<?>> getDriverClasses() {
                    try {
                        // We need to get the field value every time, because in JRE 1.6, the Vector
                        // is replaced when a new driver is added.
                        Vector<?> drivers = driverManager.getDrivers();
                        List<Class<?>> driverClasses = new ArrayList<Class<?>>(drivers.size());
                        for (Object driverInfo : drivers) {
                            driverClasses.add((Class<?>)driverClassField.get(driverInfo));
                        }
                        return driverClasses;
                    } catch (IllegalAccessException ex) {
                        throw new IllegalAccessError(ex.getMessage());
                    }
                }
            };
        } catch (NoSuchFieldException ex) {
            return null;
        } catch (ClassNotFoundException ex) {
            return null;
        } catch (RBeanFactoryException ex) {
            return null;
        }
    }
}
