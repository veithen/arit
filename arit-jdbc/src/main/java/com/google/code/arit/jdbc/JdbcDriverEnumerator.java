package com.google.code.arit.jdbc;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.code.arit.ResourceEnumerator;

public class JdbcDriverEnumerator implements ResourceEnumerator {
    private final Iterator<Class<?>> iterator;
    private Class<?> driverClass;
    
    public JdbcDriverEnumerator(List<Class<?>> driverClasses) {
        iterator = driverClasses.iterator();
    }

    public Collection<ClassLoader> getClassLoaders() {
        return Collections.singleton(driverClass.getClassLoader());
    }

    public String getDescription() {
        return "JDBC driver: " + driverClass.getName();
    }

    public boolean next() {
        if (iterator.hasNext()) {
            driverClass = iterator.next();
            return true;
        } else {
            return false;
        }
    }
}
