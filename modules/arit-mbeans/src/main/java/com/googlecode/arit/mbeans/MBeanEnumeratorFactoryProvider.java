package com.googlecode.arit.mbeans;

import java.util.List;

import com.googlecode.arit.Provider;
import com.googlecode.arit.ProviderFinder;
import com.googlecode.arit.ResourceEnumeratorFactory;

public class MBeanEnumeratorFactoryProvider implements Provider<ResourceEnumeratorFactory> {
    public ResourceEnumeratorFactory getImplementation() {
        List<MBeanServerInspector> inspectors = ProviderFinder.find(MBeanServerInspector.class);
        return inspectors.isEmpty() ? null : new MBeanEnumeratorFactory(inspectors);
    }
}
