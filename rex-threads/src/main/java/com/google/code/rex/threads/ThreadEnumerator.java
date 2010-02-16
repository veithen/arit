package com.google.code.rex.threads;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.code.rex.ResourceEnumerator;

public class ThreadEnumerator implements ResourceEnumerator {
    private final Thread[] threads;
    private int current = -1;
    private Thread thread;
    private ThreadDescription description;

    public ThreadEnumerator(Thread[] threads) {
        this.threads = threads;
    }

    public Collection<ClassLoader> getClassLoaders() {
        return description == null ? Collections.singleton(thread.getContextClassLoader()) : description.getClassLoaders();
    }

    public String getDescription() {
        return description == null ? "Thread: " + thread.getName() + " [" + thread.getId() + "] " : description.getDescription();
    }

    public boolean next() {
        if (current+1 < threads.length) {
            current++;
            thread = threads[current];
            description = ThreadInspectors.getInstance().getDescription(thread);
            return true;
        } else {
            return false;
        }
    }
}
