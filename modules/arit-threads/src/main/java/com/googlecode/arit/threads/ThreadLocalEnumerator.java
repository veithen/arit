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
package com.googlecode.arit.threads;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.googlecode.arit.ResourceEnumerator;

public class ThreadLocalEnumerator implements ResourceEnumerator {
    private final Iterator<Set<Class<?>>> iterator;
    private Set<Class<?>> classes;

    public ThreadLocalEnumerator(Map<ThreadLocal<?>,Set<Class<?>>> threadLocals) {
        iterator = threadLocals.values().iterator();
    }

    public Collection<ClassLoader> getClassLoaders() {
        Set<ClassLoader> classLoaders = new HashSet<ClassLoader>();
        for (Class<?> clazz : classes) {
            classLoaders.add(clazz.getClassLoader());
        }
        return classLoaders;
    }

    public String getDescription() {
        return "Thread local; value classes: " + classes;
    }

    public boolean next() {
        if (iterator.hasNext()) {
            classes = iterator.next();
            return true;
        } else {
            return false;
        }
    }
}
