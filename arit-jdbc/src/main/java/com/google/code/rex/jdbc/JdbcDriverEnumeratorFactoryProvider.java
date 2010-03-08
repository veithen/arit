package com.google.code.rex.jdbc;

import java.util.List;

import com.google.code.rex.Provider;
import com.google.code.rex.ProviderFinder;
import com.google.code.rex.ResourceEnumeratorFactory;

public class JdbcDriverEnumeratorFactoryProvider implements Provider<ResourceEnumeratorFactory> {
    public ResourceEnumeratorFactory getImplementation() {
        List<DriverManagerInspector> inspectors = ProviderFinder.find(DriverManagerInspector.class);
        if (inspectors.isEmpty()) {
            return null;
        } else {
            return new JdbcDriverEnumeratorFactory(inspectors.get(0));
        }
    }
}
