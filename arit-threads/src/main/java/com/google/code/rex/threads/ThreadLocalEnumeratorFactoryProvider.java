package com.google.code.rex.threads;

import java.util.List;

import com.google.code.rex.Provider;
import com.google.code.rex.ProviderFinder;
import com.google.code.rex.ResourceEnumeratorFactory;

public class ThreadLocalEnumeratorFactoryProvider implements Provider<ResourceEnumeratorFactory> {
    public ResourceEnumeratorFactory getImplementation() {
        List<ThreadLocalInspector> inspectors = ProviderFinder.find(ThreadLocalInspector.class);
        return inspectors.isEmpty() ? null : new ThreadLocalEnumeratorFactory(inspectors.get(0));
    }
}
