package com.google.code.rex.threads;

import java.util.ArrayList;
import java.util.List;

import com.google.code.rex.ResourceEnumerator;
import com.google.code.rex.ResourceEnumeratorFactory;

public class ThreadEnumeratorFactory implements ResourceEnumeratorFactory {
    private void visit(ThreadGroup threadGroup, List<Thread> threadList) {
        int numThreads = threadGroup.activeCount();
        Thread[] threads = new Thread[numThreads*2];
        numThreads = threadGroup.enumerate(threads, false);
        for (int i=0; i<numThreads; i++) {
            threadList.add(threads[i]);
        }

        int numGroups = threadGroup.activeGroupCount();
        ThreadGroup[] groups = new ThreadGroup[numGroups*2];
        numGroups = threadGroup.enumerate(groups, false);
        for (int i=0; i<numGroups; i++) {
            visit(groups[i], threadList);
        }
    }

    public ResourceEnumerator createEnumerator() {
        ThreadGroup tg = Thread.currentThread().getThreadGroup();
        ThreadGroup parent;
        while ((parent = tg.getParent()) != null) {
            tg = parent;
        }
        List<Thread> threads = new ArrayList<Thread>(50);
        visit(tg, threads);
        return new ThreadEnumerator(threads);
    }
}
