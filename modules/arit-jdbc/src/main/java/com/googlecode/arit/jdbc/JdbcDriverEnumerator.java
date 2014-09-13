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
package com.googlecode.arit.jdbc;

import java.util.Iterator;
import java.util.List;

import com.googlecode.arit.Formatter;
import com.googlecode.arit.resource.ResourceType;
import com.googlecode.arit.resource.SimpleResourceEnumerator;

public class JdbcDriverEnumerator extends SimpleResourceEnumerator {
    private final ResourceType resourceType;
    private final Iterator<Class<?>> iterator;
    private Class<?> driverClass;
    
    public JdbcDriverEnumerator(ResourceType resourceType, List<Class<?>> driverClasses) {
        this.resourceType = resourceType;
        iterator = driverClasses.iterator();
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public ClassLoader getReferencedClassLoader() {
        return driverClass.getClassLoader();
    }

    public String getClassLoaderReferenceDescription(Formatter formatter) {
        return "Driver class";
    }

    public Object getResourceObject() {
        // TODO: this is probably not correct
        return driverClass;
    }

    public String getResourceDescription(Formatter formatter) {
        return "JDBC driver: " + driverClass.getName();
    }

    protected boolean doNextResource() {
        if (iterator.hasNext()) {
            driverClass = iterator.next();
            return true;
        } else {
            return false;
        }
    }

    public boolean cleanup() {
        return false;
    }
}
