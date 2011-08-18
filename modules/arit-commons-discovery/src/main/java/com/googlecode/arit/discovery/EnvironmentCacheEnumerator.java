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
package com.googlecode.arit.discovery;

import java.util.Iterator;
import java.util.Map;

import com.googlecode.arit.Formatter;
import com.googlecode.arit.ResourceEnumerator;
import com.googlecode.arit.ResourceType;

public class EnvironmentCacheEnumerator implements ResourceEnumerator {
    private final ResourceType resourceType;
    private final Iterator<Map.Entry<ClassLoader,Map<String,Object>>> rootCacheIterator;
    private Iterator<Map.Entry<String,Object>> spIterator;
    private ClassLoader classLoader;
    private String spName;
    private Object service;
    private int state;
    
    public EnvironmentCacheEnumerator(ResourceType resourceType, Map<ClassLoader,Map<String,Object>> rootCache) {
        this.resourceType = resourceType;
        rootCacheIterator = rootCache.entrySet().iterator();
    }

    public boolean nextResource() {
        while (true) {
            if (spIterator == null) {
                if (rootCacheIterator.hasNext()) {
                    Map.Entry<ClassLoader,Map<String,Object>> entry = rootCacheIterator.next();
                    classLoader = entry.getKey();
                    spIterator = entry.getValue().entrySet().iterator();
                } else {
                    return false;
                }
            } else if (spIterator.hasNext()) {
                Map.Entry<String,Object> entry = spIterator.next();
                spName = entry.getKey();
                service = entry.getValue();
                state = -1;
                return true;
            } else {
                spIterator = null;
            }
        }
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public Object getResourceObject() {
        return service;
    }

    public String getResourceDescription(Formatter formatter) {
        return "Cached service provider (commons-discovery): " + spName;
    }

    public boolean nextClassLoaderReference() {
        state++;
        return state < 2;
    }
    
    public ClassLoader getReferencedClassLoader() {
        switch (state) {
            case 0: return classLoader;
            case 1: return service.getClass().getClassLoader();
            default: throw new IllegalStateException();
        }
    }

    public String getClassLoaderReferenceDescription(Formatter formatter) {
        switch (state) {
            case 0: return "Class loader key";
            case 1: return "Service implementation: " + service.getClass().getName();
            default: throw new IllegalStateException();
        }
    }

    public boolean cleanup() {
        // TODO
        return false;
    }
}
