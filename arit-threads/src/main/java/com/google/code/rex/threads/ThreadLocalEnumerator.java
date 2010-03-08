package com.google.code.rex.threads;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.code.rex.ResourceEnumerator;

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
