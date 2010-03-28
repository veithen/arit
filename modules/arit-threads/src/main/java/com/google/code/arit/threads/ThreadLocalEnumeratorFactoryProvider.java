package com.google.code.arit.threads;

import java.util.List;

import com.google.code.arit.Provider;
import com.google.code.arit.ProviderFinder;
import com.google.code.arit.ResourceEnumeratorFactory;

public class ThreadLocalEnumeratorFactoryProvider implements Provider<ResourceEnumeratorFactory> {
    public ResourceEnumeratorFactory getImplementation() {
        List<ThreadLocalInspector> inspectors = ProviderFinder.find(ThreadLocalInspector.class);
        return inspectors.isEmpty() ? null : new ThreadLocalEnumeratorFactory(inspectors.get(0));
    }
}
