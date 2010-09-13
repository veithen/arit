/*
 * Copyright 2010 Andreas Veithen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.arit.mbeans;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.codehaus.plexus.logging.Logger;

import com.googlecode.arit.ResourceEnumerator;

public class MBeanEnumerator implements ResourceEnumerator {
    private final List<MBeanServerInspectorPlugin> mbsInspectors; 
    private final Iterator<MBeanServer> mbsIterator;
    private final Logger logger;
    private MBeanRepository repository;
    private Iterator<ObjectName> mbeanIterator;
    private ObjectName name;
    private Object mbean;

    public MBeanEnumerator(List<MBeanServerInspectorPlugin> mbsInspectors, Iterator<MBeanServer> mbsIterator, Logger logger) {
        this.mbsInspectors = mbsInspectors;
        this.mbsIterator = mbsIterator;
        this.logger = logger;
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
                for (MBeanServerInspectorPlugin inspector : mbsInspectors) {
                    repository = inspector.inspect(mbs);
                    if (repository == null) {
                        logger.error("Unable to inspect MBeanServer of type " + mbs.getClass().getName());
                    } else {
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
