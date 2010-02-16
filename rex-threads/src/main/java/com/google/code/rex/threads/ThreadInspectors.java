package com.google.code.rex.threads;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.google.code.rex.ProviderFinder;

public class ThreadInspectors {
    private static ThreadInspectors instance;
    
    private final List<ThreadInspector> inspectors;
    
    private ThreadInspectors(List<ThreadInspector> inspectors) {
        this.inspectors = inspectors;
    }
    
    public static synchronized ThreadInspectors getInstance() {
        if (instance == null) {
            List<ThreadInspector> inspectors = ProviderFinder.find(ThreadInspector.class);
            Collections.sort(inspectors, new Comparator<ThreadInspector>() {
                public int compare(ThreadInspector o1, ThreadInspector o2) {
                    return o2.getPriority()-o1.getPriority();
                }
            });
            instance = new ThreadInspectors(inspectors);
        }
        return instance;
    }

    public ThreadDescription getDescription(Thread thread) {
        for (ThreadInspector inspector : inspectors) {
            ThreadDescription description = inspector.getDescription(thread);
            if (description != null) {
                return description;
            }
        }
        return null;
    }
}
