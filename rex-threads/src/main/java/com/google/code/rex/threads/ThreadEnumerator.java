package com.google.code.rex.threads;

import java.util.Iterator;
import java.util.List;

import com.google.code.rex.ResourceEnumerator;

public class ThreadEnumerator implements ResourceEnumerator {
    private final Iterator<Thread> iterator;
    private Thread thread;

    public ThreadEnumerator(List<Thread> threads) {
        iterator = threads.iterator();
    }

    public ClassLoader getClassLoader() {
        return thread.getContextClassLoader();
    }

    public String getDescription() {
        return "Thread: " + thread.getName() + " [" + thread.getId() + "]";
    }

    public boolean next() {
        if (iterator.hasNext()) {
            thread = iterator.next();
            return true;
        } else {
            return false;
        }
    }
}
