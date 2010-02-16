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
        return new ThreadEnumerator(ThreadUtils.getAllThreads(), threadInspectors);
    }
}
