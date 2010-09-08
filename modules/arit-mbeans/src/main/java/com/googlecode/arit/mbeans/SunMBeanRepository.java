package com.googlecode.arit.mbeans;

import javax.management.ObjectName;

import com.sun.jmx.mbeanserver.Repository;

public class SunMBeanRepository implements MBeanRepository {
    private final Repository repository;

    public SunMBeanRepository(Repository repository) {
        this.repository = repository;
    }

    public Object retrieve(ObjectName name) {
        return repository.retrieve(name);
    }
}
