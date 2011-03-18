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
package com.googlecode.arit.threadlocals;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.googlecode.arit.ResourceEnumerator;
import com.googlecode.arit.ResourceType;

public class ThreadLocalEnumerator implements ResourceEnumerator {
    private final ResourceType resourceType;
    private final Iterator<Set<Class<?>>> threadLocalIterator;
    private Iterator<Class<?>> valueClassIterator;
    private Class<?> valueClass;

    public ThreadLocalEnumerator(ResourceType resourceType, Map<ThreadLocal<?>,Set<Class<?>>> threadLocals) {
        this.resourceType = resourceType;
        threadLocalIterator = threadLocals.values().iterator();
    }

    public ResourceType getType() {
        return resourceType;
    }

    public String getResourceDescription() {
        return "Thread local";
    }

    public boolean nextResource() {
        if (threadLocalIterator.hasNext()) {
            valueClassIterator = threadLocalIterator.next().iterator();
            return true;
        } else {
            return false;
        }
    }

    public boolean nextClassLoaderReference() {
        if (valueClassIterator.hasNext()) {
            valueClass = valueClassIterator.next();
            return true;
        } else {
            return false;
        }
    }

    public ClassLoader getReferencedClassLoader() {
        return valueClass.getClassLoader();
    }

    public String getClassLoaderReferenceDescription() {
        return "Value class: " + valueClass.getName();
    }
}
