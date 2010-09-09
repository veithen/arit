package com.googlecode.arit.mbeans;

import javax.management.ObjectName;

import com.sun.jmx.mbeanserver.Repository;

public class SunJava5MBeanRepository implements MBeanRepository {
    private final Repository repository;

    public SunJava5MBeanRepository(Repository repository) {
        this.repository = repository;
    }

    public Object retrieve(ObjectName name) {
        return repository.retrieve(name);
    }
}
