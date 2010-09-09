package com.googlecode.arit.jdbc;

import java.util.List;

import com.googlecode.arit.Provider;
import com.googlecode.arit.ProviderFinder;
import com.googlecode.arit.ResourceEnumeratorFactory;

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
