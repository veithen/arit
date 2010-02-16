package com.google.code.rex.jdbc;

import java.lang.reflect.Field;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.google.code.rex.Provider;

public class DefaultDriverManagerInspectorProvider implements Provider<DriverManagerInspector> {
    public DriverManagerInspector getImplementation() {
        try {
            Field driversField = DriverManager.class.getDeclaredField("drivers");
            driversField.setAccessible(true);
            final Vector<?> drivers = (Vector<?>)driversField.get(null);
            final Field driverClassField = Class.forName("java.sql.DriverInfo").getDeclaredField("driverClass");
            driverClassField.setAccessible(true);
            return new DriverManagerInspector() {
                public List<Class<?>> getDriverClasses() {
                    List<Class<?>> driverClasses = new ArrayList<Class<?>>(drivers.size());
                    for (Object driverInfo : drivers) {
                        try {
                            driverClasses.add((Class<?>)driverClassField.get(driverInfo));
                        } catch (IllegalAccessException ex) {
                            throw new IllegalAccessError(ex.getMessage());
                        }
                    }
                    return driverClasses;
                }
            };
        } catch (NoSuchFieldException ex) {
            return null;
        } catch (IllegalAccessException ex) {
            return null;
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }
}
