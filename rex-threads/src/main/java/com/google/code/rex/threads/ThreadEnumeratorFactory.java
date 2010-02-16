package com.google.code.rex.threads;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.code.rex.ProviderFinder;
import com.google.code.rex.ResourceEnumerator;
import com.google.code.rex.ResourceEnumeratorFactory;

public class ThreadEnumeratorFactory implements ResourceEnumeratorFactory {
    private final List<ThreadInspector> threadInspectors;
    
    public ThreadEnumeratorFactory() {
        threadInspectors = ProviderFinder.find(ThreadInspector.class);
        Collections.sort(threadInspectors, new Comparator<ThreadInspector>() {
            public int compare(ThreadInspector o1, ThreadInspector o2) {
                return o2.getPriority()-o1.getPriority();
            }
        });
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
        return new ThreadEnumerator(threads, threadCount, threadInspectors);
    }
}
