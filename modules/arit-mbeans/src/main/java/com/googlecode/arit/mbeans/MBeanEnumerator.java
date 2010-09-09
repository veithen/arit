package com.googlecode.arit.mbeans;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.google.code.arit.ResourceEnumerator;

public class MBeanEnumerator implements ResourceEnumerator {
    private final List<MBeanServerInspector> mbsInspectors; 
    private final Iterator<MBeanServer> mbsIterator;
    private MBeanRepository repository;
    private Iterator<ObjectName> mbeanIterator;
    private ObjectName name;
    private Object mbean;

    public MBeanEnumerator(List<MBeanServerInspector> mbsInspectors, Iterator<MBeanServer> mbsIterator) {
        this.mbsInspectors = mbsInspectors;
        this.mbsIterator = mbsIterator;
    }

    public Collection<ClassLoader> getClassLoaders() {
        return Collections.singleton(mbean.getClass().getClassLoader());
    }

    public String getDescription() {
        return "MBean: " + name.toString() + " (class: " + mbean.getClass().getName() + ")";
    }

    public boolean next() {
        while (true) {
            if (mbeanIterator != null && mbeanIterator.hasNext()) {
                name = mbeanIterator.next();
                mbean = repository.retrieve(name);
                return true;
            } else if (mbsIterator.hasNext()) {
                mbeanIterator = null;
                MBeanServer mbs = mbsIterator.next();
                for (MBeanServerInspector inspector : mbsInspectors) {
                    repository = inspector.inspect(mbs);
                    if (repository != null) {
                        try {
                            mbeanIterator = mbs.queryNames(new ObjectName("*:*"), null).iterator();
                        } catch (MalformedObjectNameException ex) {
                            // We should never get here
                            throw new Error(ex);
                        }
                        break;
                    }
                }
            } else {
                return false;
            }
        }
    }
}
