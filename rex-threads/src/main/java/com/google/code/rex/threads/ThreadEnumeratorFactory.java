package com.google.code.rex.threads;

import com.google.code.rex.ResourceEnumerator;
import com.google.code.rex.ResourceEnumeratorFactory;

public class ThreadEnumeratorFactory implements ResourceEnumeratorFactory {
    public ResourceEnumerator createEnumerator() {
        return new ThreadEnumerator(ThreadUtils.getAllThreads());
    }
}
