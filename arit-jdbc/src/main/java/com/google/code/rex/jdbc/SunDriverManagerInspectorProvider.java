package com.google.code.rex.jdbc;

import java.lang.reflect.Field;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.google.code.rex.Provider;
import com.google.code.rex.util.ReflectionUtil;

public class SunDriverManagerInspectorProvider implements Provider<DriverManagerInspector> {
    public DriverManagerInspector getImplementation() {
        try {
            // Java 1.5 uses "drivers" attribute.
            // Java 1.6 has some copy-on-write feature and uses "readDrivers".
            final Field driversField = ReflectionUtil.getField(DriverManager.class, "drivers", "readDrivers");
            final Field driverClassField = ReflectionUtil.getField(Class.forName("java.sql.DriverInfo"), "driverClass");
            return new DriverManagerInspector() {
                public List<Class<?>> getDriverClasses() {
                    try {
                        // We need to get the field value every time, because in JRE 1.6, the Vector
                        // is replaced when a new driver is added.
                        Vector<?> drivers = (Vector<?>)driversField.get(null);
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
        }
    }
}
