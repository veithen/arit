package com.googlecode.arit.mbeans;

import java.util.List;

import javax.management.MBeanServerFactory;

import com.google.code.arit.ResourceEnumerator;
import com.google.code.arit.ResourceEnumeratorFactory;

public class MBeanEnumeratorFactory implements ResourceEnumeratorFactory {
    private final List<MBeanServerInspector> mbsInspectors; 
    
    public MBeanEnumeratorFactory(List<MBeanServerInspector> mbsInspectors) {
        this.mbsInspectors = mbsInspectors;
    }

    public String getDescription() {
        return "MBeans";
    }

    public ResourceEnumerator createEnumerator() {
        return new MBeanEnumerator(mbsInspectors, MBeanServerFactory.findMBeanServer(null).iterator());
    }
}
