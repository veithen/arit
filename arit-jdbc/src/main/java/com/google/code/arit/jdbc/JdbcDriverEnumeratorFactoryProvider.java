package com.google.code.arit.jdbc;

import java.util.List;

import com.google.code.arit.Provider;
import com.google.code.arit.ProviderFinder;
import com.google.code.arit.ResourceEnumeratorFactory;

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
