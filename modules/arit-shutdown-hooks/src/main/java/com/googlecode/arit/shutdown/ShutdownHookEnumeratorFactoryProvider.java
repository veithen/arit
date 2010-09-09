package com.googlecode.arit.shutdown;

import java.util.List;

import com.googlecode.arit.Provider;
import com.googlecode.arit.ProviderFinder;
import com.googlecode.arit.ResourceEnumeratorFactory;

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
