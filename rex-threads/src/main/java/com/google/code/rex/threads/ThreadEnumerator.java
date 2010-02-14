package com.google.code.rex.threads;

import com.google.code.rex.ResourceEnumerator;

public class ThreadEnumerator implements ResourceEnumerator {
    private final Thread[] threads;
    private final int count;
    private final ThreadInspector threadInspector;
    private int current = -1;
    private Thread thread;
    private Runnable runnable;

    public ThreadEnumerator(Thread[] threads, int count, ThreadInspector threadInspector) {
        this.threads = threads;
        this.count = count;
        this.threadInspector = threadInspector;
    }

    public ClassLoader getClassLoader() {
        return thread.getContextClassLoader();
    }

    public String getDescription() {
        return "Thread: " + thread.getName() + " [" + thread.getId() + "] " + thread.getClass() + " target=" + runnable;
    }

    public boolean next() {
        if (current+1 < count) {
            current++;
            thread = threads[current];
            runnable = threadInspector.getRunnable(thread);
            return true;
        } else {
            return false;
        }
    }
}
