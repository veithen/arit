package com.googlecode.arit.threads;

import com.googlecode.arit.ResourceEnumerator;
import com.googlecode.arit.ResourceEnumeratorFactory;

public class ThreadEnumeratorFactory implements ResourceEnumeratorFactory {
    public String getDescription() {
        return "Threads and timers";
    }

    public ResourceEnumerator createEnumerator() {
        return new ThreadEnumerator(ThreadUtils.getAllThreads());
    }
}
