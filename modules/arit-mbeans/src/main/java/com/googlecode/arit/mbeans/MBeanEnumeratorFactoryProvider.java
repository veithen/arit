package com.googlecode.arit.mbeans;

import java.util.List;

import com.google.code.arit.Provider;
import com.google.code.arit.ProviderFinder;
import com.google.code.arit.ResourceEnumeratorFactory;

public class MBeanEnumeratorFactoryProvider implements Provider<ResourceEnumeratorFactory> {
    public ResourceEnumeratorFactory getImplementation() {
        List<MBeanServerInspector> inspectors = ProviderFinder.find(MBeanServerInspector.class);
        return inspectors.isEmpty() ? null : new MBeanEnumeratorFactory(inspectors);
    }
}
