package com.googlecode.arit.threads;

import java.util.List;

import com.googlecode.arit.Provider;
import com.googlecode.arit.ProviderFinder;
import com.googlecode.arit.ResourceEnumeratorFactory;

public class ThreadLocalEnumeratorFactoryProvider implements Provider<ResourceEnumeratorFactory> {
    public ResourceEnumeratorFactory getImplementation() {
        List<ThreadLocalInspector> inspectors = ProviderFinder.find(ThreadLocalInspector.class);
        return inspectors.isEmpty() ? null : new ThreadLocalEnumeratorFactory(inspectors.get(0));
    }
}
