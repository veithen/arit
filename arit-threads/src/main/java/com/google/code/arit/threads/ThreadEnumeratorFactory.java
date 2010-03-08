package com.google.code.arit.threads;

import com.google.code.arit.ResourceEnumerator;
import com.google.code.arit.ResourceEnumeratorFactory;

public class ThreadEnumeratorFactory implements ResourceEnumeratorFactory {
    public ResourceEnumerator createEnumerator() {
        return new ThreadEnumerator(ThreadUtils.getAllThreads());
    }
}
