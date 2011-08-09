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

import com.googlecode.arit.ResourceType;
import com.googlecode.arit.SimpleResourceEnumerator;

public class PerClassLoaderCacheResourceEnumerator extends SimpleResourceEnumerator {
    private final ResourceType resourceType;
    private final String description;
    private final Iterator<ClassLoader> classLoaderIterator;
    private ClassLoader classLoader;

    public PerClassLoaderCacheResourceEnumerator(ResourceType resourceType, String description, Map<ClassLoader,?> cache) {
        this.resourceType = resourceType;
        this.description = description;
        classLoaderIterator = cache.keySet().iterator();
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public ClassLoader getReferencedClassLoader() {
        return classLoader;
    }

    public String getClassLoaderReferenceDescription() {
        return "Cache key";
    }

    public Object getResourceObject() {
        return classLoader;
    }

    public String getResourceDescription() {
        return description;
    }

    protected boolean doNextResource() {
        if (classLoaderIterator.hasNext()) {
            classLoader = classLoaderIterator.next();
            return true;
        } else {
            return false;
        }
    }

    public boolean cleanup() {
        classLoaderIterator.remove();
        return true;
    }
}
