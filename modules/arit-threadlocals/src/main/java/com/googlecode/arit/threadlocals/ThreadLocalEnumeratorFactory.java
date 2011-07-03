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

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import com.googlecode.arit.ResourceEnumeratorFactory;
import com.googlecode.arit.ResourceType;
import com.googlecode.arit.threadutils.ThreadUtils;

public class ThreadLocalEnumeratorFactory implements ResourceEnumeratorFactory<ThreadLocalEnumerator> {
    @Resource(name="threadlocal")
    private ResourceType resourceType;
    
    @Autowired
    private ThreadLocalInspector inspector;
    
    public boolean isAvailable() {
        return inspector.isAvailable();
    }

    public String getDescription() {
        return "Thread locals";
    }

    public ThreadLocalEnumerator createEnumerator() {
        Map<ThreadLocal<?>,Set<Class<?>>> threadLocals = new IdentityHashMap<ThreadLocal<?>,Set<Class<?>>>();
        for (Thread thread : ThreadUtils.getAllThreads()) {
            for (Map.Entry<ThreadLocal<?>,Object> entry : inspector.getThreadLocalMap(thread).entrySet()) {
                ThreadLocal<?> threadLocal = entry.getKey();
                Object value = entry.getValue();
                if (value != null) {
                    Set<Class<?>> classes = threadLocals.get(threadLocal);
                    if (classes == null) {
                        classes = new HashSet<Class<?>>();
                        threadLocals.put(threadLocal, classes);
                    }
                    collectClasses(classes, value);
                }
            }
        }
        return new ThreadLocalEnumerator(resourceType, threadLocals);
    }
    
    private void collectClasses(Set<Class<?>> classes, Object value) {
        if (value instanceof Class<?>) {
            classes.add((Class<?>)value);
        } else {
            // Even if it is a collection, the actual implementation may be loaded from the
            // application class loader. Therefore we always add the class.
            classes.add(value.getClass());
            if (value instanceof Map<?,?>) {
                // This should allow us situations such as described in AXIS-2674
                try {
                    for (Map.Entry<?,?> mapEntry : ((Map<?,?>)value).entrySet()) {
                        Object mapKey = mapEntry.getKey();
                        if (mapKey != null) {
                            collectClasses(classes, mapKey);
                        }
                        Object mapValue = mapEntry.getValue();
                        if (mapValue != null) {
                            collectClasses(classes, mapValue);
                        }
                    }
                } catch (UnsupportedOperationException ex) {
                    return;
                }
            }
        }
    }
}
