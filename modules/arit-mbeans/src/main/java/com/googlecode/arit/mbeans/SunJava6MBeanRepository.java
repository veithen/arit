package com.googlecode.arit.mbeans;

import javax.management.ObjectName;

import com.sun.jmx.mbeanserver.DynamicMBean2;
import com.sun.jmx.mbeanserver.Repository;

public class SunJava6MBeanRepository implements MBeanRepository {
    private final Repository repository;

    public SunJava6MBeanRepository(Repository repository) {
        this.repository = repository;
    }

    public Object retrieve(ObjectName name) {
        Object object = repository.retrieve(name);
        if (object instanceof DynamicMBean2) {
            return ((DynamicMBean2)object).getResource();
        } else {
            return object;
        }
    }
}
