package com.googlecode.arit.shutdown;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.googlecode.arit.ResourceEnumerator;

public class ShutdownHookEnumerator implements ResourceEnumerator {
    private final Iterator<Thread> iterator;
    private Thread hook;
    
    public ShutdownHookEnumerator(List<Thread> hooks) {
        iterator = hooks.iterator();
    }

    public Collection<ClassLoader> getClassLoaders() {
        return Collections.singleton(hook.getClass().getClassLoader());
    }

    public String getDescription() {
        return "Shutdown hook; type=" + hook.getClass().getName();
    }

    public boolean next() {
        if (iterator.hasNext()) {
            hook = iterator.next();
            return true;
        } else {
            return false;
        }
    }
}
