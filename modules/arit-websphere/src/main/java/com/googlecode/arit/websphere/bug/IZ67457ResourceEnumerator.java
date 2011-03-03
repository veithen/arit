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
package com.googlecode.arit.websphere.bug;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import com.googlecode.arit.ResourceEnumerator;
import com.googlecode.arit.ResourceType;

public class IZ67457ResourceEnumerator implements ResourceEnumerator {
    private final ResourceType resourceType;
    private final Iterator<Class<?>> classIterator;
    private Class<?> clazz;

    public IZ67457ResourceEnumerator(ResourceType resourceType, Iterator<Class<?>> classIterator) {
        this.resourceType = resourceType;
        this.classIterator = classIterator;
    }

    public ResourceType getType() {
        return resourceType;
    }

    public Collection<ClassLoader> getClassLoaders() {
        return Collections.singleton(clazz.getClassLoader());
    }

    public String getDescription() {
        return "Cached MethodDescriptors for class " + clazz.getName();
    }

    public boolean next() {
        if (classIterator.hasNext()) {
            clazz = classIterator.next();
            return true;
        } else {
            return false;
        }
    }
}
