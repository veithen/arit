package com.google.code.rex.threads;

import com.google.code.rex.ResourceEnumerator;

public class ThreadEnumerator implements ResourceEnumerator {
    private final Thread[] threads;
    private final int count;
    private int current = -1;

    public ThreadEnumerator(Thread[] threads, int count) {
        this.threads = threads;
        this.count = count;
    }

    public ClassLoader getClassLoader() {
        return threads[current].getContextClassLoader();
    }

    public String getDescription() {
        Thread thread = threads[current];
        return "Thread: " + thread.getName() + " [" + thread.getId() + "]";
    }

    public boolean next() {
        if (current+1 < count) {
            current++;
            return true;
        } else {
            return false;
        }
    }
}
