package com.google.code.rex.jdbc;

import java.lang.reflect.Field;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.google.code.rex.Provider;
import com.google.code.rex.util.ReflectionUtil;

public class DefaultDriverManagerInspectorProvider implements Provider<DriverManagerInspector> {
    public DriverManagerInspector getImplementation() {
        try {
            Field driversField = ReflectionUtil.getField(DriverManager.class, "drivers");
            final Vector<?> drivers = (Vector<?>)driversField.get(null);
            final Field driverClassField = ReflectionUtil.getField(Class.forName("java.sql.DriverInfo"), "driverClass");
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
