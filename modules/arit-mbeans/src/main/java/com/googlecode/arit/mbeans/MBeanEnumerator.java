/*
 * Copyright 2010-2011 Andreas Veithen
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

import java.util.Iterator;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.codehaus.plexus.logging.Logger;

import com.googlecode.arit.ResourceType;
import com.googlecode.arit.SimpleResourceEnumerator;

public class MBeanEnumerator extends SimpleResourceEnumerator {
    private final ResourceType resourceType;
    private final MBeanServerInspector mbsInspector; 
    private final Iterator<MBeanServer> mbsIterator;
    private final Logger logger;
    private MBeanServer mbs;
    private MBeanAccessor mbeanAccessor;
    private Iterator<ObjectName> mbeanIterator;
    private ObjectName name;
    private Object mbean;

    public MBeanEnumerator(ResourceType resourceType, MBeanServerInspector mbsInspector, Iterator<MBeanServer> mbsIterator, Logger logger) {
        this.resourceType = resourceType;
        this.mbsInspector = mbsInspector;
        this.mbsIterator = mbsIterator;
        this.logger = logger;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public ClassLoader getReferencedClassLoader() {
        return mbean.getClass().getClassLoader();
    }

    public String getClassLoaderReferenceDescription() {
        return "MBean implementation class";
    }

    public String getResourceDescription() {
        return "MBean: " + name.toString() + " (class: " + mbean.getClass().getName() + ")";
    }

    protected boolean doNextResource() {
        while (true) {
            if (mbeanIterator != null && mbeanIterator.hasNext()) {
                name = mbeanIterator.next();
                mbean = mbeanAccessor.retrieve(name);
                return true;
            } else if (mbsIterator.hasNext()) {
                mbeanIterator = null;
                mbs = mbsIterator.next();
                mbeanAccessor = mbsInspector.inspect(mbs);
                if (mbeanAccessor == null) {
                    logger.error("Unable to inspect MBeanServer of type " + mbs.getClass().getName());
                } else {
                    try {
                        mbeanIterator = mbs.queryNames(new ObjectName("*:*"), null).iterator();
                    } catch (MalformedObjectNameException ex) {
                        // We should never get here
                        throw new Error(ex);
                    }
                }
            } else {
                return false;
            }
        }
    }

    public boolean cleanup() {
        try {
            mbs.unregisterMBean(name);
            return true;
        } catch (JMException ex) {
            return false;
        }
    }
}
