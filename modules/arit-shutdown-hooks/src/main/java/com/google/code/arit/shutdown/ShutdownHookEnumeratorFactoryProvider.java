package com.google.code.arit.shutdown;

import java.util.List;

import com.google.code.arit.Provider;
import com.google.code.arit.ProviderFinder;
import com.google.code.arit.ResourceEnumeratorFactory;

public class ShutdownHookEnumeratorFactoryProvider implements Provider<ResourceEnumeratorFactory> {
    public ResourceEnumeratorFactory getImplementation() {
        List<ShutdownHookInspector> inspectors = ProviderFinder.find(ShutdownHookInspector.class);
        if (inspectors.isEmpty()) {
            return null;
        } else {
            return new ShutdownHookEnumeratorFactory(inspectors.get(0));
        }
    }
}
