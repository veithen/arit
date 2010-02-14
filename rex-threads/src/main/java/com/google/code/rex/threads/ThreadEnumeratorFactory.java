package com.google.code.rex.threads;

import com.google.code.rex.ResourceEnumerator;
import com.google.code.rex.ResourceEnumeratorFactory;

public class ThreadEnumeratorFactory implements ResourceEnumeratorFactory {
    private final ThreadInspector threadInspector;
    
    public ThreadEnumeratorFactory() {
        this.threadInspector = new ThreadInspector();
    }
    
    public ResourceEnumerator createEnumerator() {
        ThreadGroup rootThreadGroup = Thread.currentThread().getThreadGroup();
        ThreadGroup parent;
        while ((parent = rootThreadGroup.getParent()) != null) {
            rootThreadGroup = parent;
        }
        Thread[] threads = new Thread[64];
        int threadCount;
        while (true) {
            threadCount = rootThreadGroup.enumerate(threads);
            if (threadCount == threads.length) {
                // We probably missed threads; double the size of the array
                threads = new Thread[threads.length*2];
            } else {
                break;
            }
        }
        return new ThreadEnumerator(threads, threadCount, threadInspector);
    }
}
