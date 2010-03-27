package com.google.code.arit.threads;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import com.google.code.arit.ResourceEnumerator;
import com.google.code.arit.ResourceEnumeratorFactory;

public class ThreadLocalEnumeratorFactory implements ResourceEnumeratorFactory {
    private final ThreadLocalInspector threadLocalInspector;

    public ThreadLocalEnumeratorFactory(ThreadLocalInspector threadLocalInspector) {
        this.threadLocalInspector = threadLocalInspector;
    }

    public String getDescription() {
        return "Thread locals";
    }

    public ResourceEnumerator createEnumerator() {
        Map<ThreadLocal<?>,Set<Class<?>>> threadLocals = new IdentityHashMap<ThreadLocal<?>,Set<Class<?>>>();
        for (Thread thread : ThreadUtils.getAllThreads()) {
            for (Map.Entry<ThreadLocal<?>,Object> entry : threadLocalInspector.getThreadLocalMap(thread).entrySet()) {
                ThreadLocal<?> threadLocal = entry.getKey();
                Object value = entry.getValue();
                if (value != null) {
                    Set<Class<?>> classes = threadLocals.get(threadLocal);
                    if (classes == null) {
                        classes = new HashSet<Class<?>>();
                        threadLocals.put(threadLocal, classes);
                    }
                    classes.add(value.getClass());
                }
            }
        }
        return new ThreadLocalEnumerator(threadLocals);
    }
}
