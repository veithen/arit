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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.googlecode.arit.Logger;
import com.googlecode.arit.ResourceEnumeratorFactory;
import com.googlecode.arit.ResourceType;
import com.googlecode.arit.threadutils.ThreadUtils;

public class ThreadLocalEnumeratorFactory implements ResourceEnumeratorFactory<ThreadLocalEnumerator> {
    @Autowired
    @Qualifier("threadlocal")
    private ResourceType resourceType;
    
    @Autowired
    private ThreadLocalInspector threadLocalInspector;
    
    @Autowired
    private ThreadLocalValueInspector threadLocalValueInspector;
    
    public boolean isAvailable() {
        return threadLocalInspector.isAvailable();
    }

    public String getDescription() {
        return "Thread locals";
    }

    public ThreadLocalEnumerator createEnumerator(Logger logger) {
        Map<ThreadLocal<?>,Set<ThreadLocalValueDescription>> threadLocals = new IdentityHashMap<ThreadLocal<?>,Set<ThreadLocalValueDescription>>();
        for (Thread thread : ThreadUtils.getAllThreads()) {
            for (Map.Entry<ThreadLocal<?>,Object> entry : threadLocalInspector.getThreadLocalMap(thread).entrySet()) {
                ThreadLocal<?> threadLocal = entry.getKey();
                Object value = entry.getValue();
                if (value != null) {
                    Set<ThreadLocalValueDescription> descriptions = threadLocals.get(threadLocal);
                    if (descriptions == null) {
                        descriptions = new HashSet<ThreadLocalValueDescription>();
                        threadLocals.put(threadLocal, descriptions);
                    }
                    collectValues(descriptions, value);
                }
            }
        }
        return new ThreadLocalEnumerator(resourceType, threadLocals);
    }
    
    private void collectValues(Set<ThreadLocalValueDescription> descriptions, Object value) {
        if (value instanceof Class<?>) {
            descriptions.add(new SimpleThreadLocalValueDescription((Class<?>)value));
        } else {
            // Even if it is a collection, the actual implementation may be loaded from the
            // application class loader. Therefore we always add the class.
            descriptions.add(new SimpleThreadLocalValueDescription(value.getClass()));
            threadLocalValueInspector.identify(descriptions, value);
            // This should allow us to identify situations such as described in AXIS-2674
            try {
                if (value instanceof Iterable<?>) {
                    for (Object item : (Iterable<?>)value) {
                        if (item != null) {
                            collectValues(descriptions, item);
                        }
                    }
                } else if (value instanceof Map<?,?>) {
                    for (Map.Entry<?,?> mapEntry : ((Map<?,?>)value).entrySet()) {
                        Object mapKey = mapEntry.getKey();
                        if (mapKey != null) {
                            collectValues(descriptions, mapKey);
                        }
                        Object mapValue = mapEntry.getValue();
                        if (mapValue != null) {
                            collectValues(descriptions, mapValue);
                        }
                    }
                }
            } catch (UnsupportedOperationException ex) {
                return;
            }
        }
    }
}
