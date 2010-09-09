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
        return ((DynamicMBean2)repository.retrieve(name)).getResource();
    }
}
