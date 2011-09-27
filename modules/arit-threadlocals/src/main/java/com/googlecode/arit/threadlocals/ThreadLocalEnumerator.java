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

import com.googlecode.arit.Formatter;
import com.googlecode.arit.ResourceEnumerator;
import com.googlecode.arit.ResourceType;

public class ThreadLocalEnumerator implements ResourceEnumerator {
    private final ResourceType resourceType;
    private final Iterator<Map.Entry<ThreadLocal<?>,Set<ThreadLocalValueDescription>>> threadLocalIterator;
    private ThreadLocal<?> threadLocal;
    private int state;
    private Iterator<ThreadLocalValueDescription> valueClassIterator;
    private ThreadLocalValueDescription valueClass;

    public ThreadLocalEnumerator(ResourceType resourceType, Map<ThreadLocal<?>,Set<ThreadLocalValueDescription>> threadLocals) {
        this.resourceType = resourceType;
        threadLocalIterator = threadLocals.entrySet().iterator();
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public String getResourceDescription(Formatter formatter) {
        return "Thread local";
    }

    public boolean nextResource() {
        if (threadLocalIterator.hasNext()) {
            Map.Entry<ThreadLocal<?>,Set<ThreadLocalValueDescription>> entry = threadLocalIterator.next();
            threadLocal = entry.getKey();
            state = -1;
            valueClassIterator = entry.getValue().iterator();
            return true;
        } else {
            return false;
        }
    }

    public Object getResourceObject() {
        return threadLocal;
    }

    public boolean nextClassLoaderReference() {
        switch (state) {
            case -1:
                state = 0;
                return true;
            case 0:
                state = 1;
                // Fall through
            case 1:
                if (valueClassIterator.hasNext()) {
                    valueClass = valueClassIterator.next();
                    return true;
                } else {
                    return false;
                }
           default:
               throw new IllegalStateException();
        }
    }

    public ClassLoader getReferencedClassLoader() {
        switch (state) {
            case 0: return threadLocal.getClass().getClassLoader();
            case 1: return valueClass.getClassLoader();
            default:
                throw new IllegalStateException();
        }
    }

    public String getClassLoaderReferenceDescription(Formatter formatter) {
        switch (state) {
            case 0: return "ThreadLocal implementation: " + threadLocal.getClass().getName();
            case 1: return valueClass.getDescription();
            default:
                throw new IllegalStateException();
        }
    }

    public boolean cleanup() {
        return false;
    }
}
