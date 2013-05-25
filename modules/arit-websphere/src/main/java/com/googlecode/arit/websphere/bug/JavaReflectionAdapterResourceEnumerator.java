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
package com.googlecode.arit.websphere.bug;

import java.util.Iterator;
import java.util.Map;

import com.github.veithen.rbeans.RBeanFactory;
import com.googlecode.arit.Formatter;
import com.googlecode.arit.ResourceEnumerator;
import com.googlecode.arit.ResourceType;

public class JavaReflectionAdapterResourceEnumerator implements ResourceEnumerator {
    private final ResourceType resourceType;
    private final RBeanFactory rbf;
    private final Iterator<Map.Entry<Object,Object>> iterator;
    private int state = -1;
    private Object key;
    private Class<?> clazz;
    private Object adapter;
    
    public JavaReflectionAdapterResourceEnumerator(ResourceType resourceType, RBeanFactory rbf, Map<Object,Object> cache) {
        this.resourceType = resourceType;
        this.rbf = rbf;
        iterator = cache.entrySet().iterator();
    }

    public boolean nextResource() {
        if (iterator.hasNext()) {
            Map.Entry<Object,Object> entry = iterator.next();
            key = entry.getKey();
            adapter = entry.getValue();
            // In the original JavaReflectionAdapter code, the key was a Class object
            // (causing the class loader leak). The interim fix changes this to a String.
            if (key instanceof String) {
                clazz = rbf.createRBean(JavaReflectionAdapterRBean.class, adapter).getClazz();
            } else {
                clazz = (Class<?>)key;
            }
            state = -1;
            return true;
        } else {
            return false;
        }
    }

    public Object getResourceObject() {
        return adapter;
    }

    public String getResourceDescription(Formatter formatter) {
        return "Cached JavaReflectionAdapter for class " + clazz.getName();
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public boolean nextClassLoaderReference() {
        if (state == 1) {
            return false;
        } else {
            state++;
            if (state == 0 && key instanceof String) {
                state = 1;
            }
            return true;
        }
    }
    
    public ClassLoader getReferencedClassLoader() {
        return clazz.getClassLoader();
    }

    public String getClassLoaderReferenceDescription(Formatter formatter) {
        switch (state) {
            case 0: return "Cache key";
            case 1: return "JavaReflectionAdapter instance";
            default:
                throw new IllegalStateException();
        }
    }

    public boolean cleanup() {
        return false;
    }
}
