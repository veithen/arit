package com.google.code.arit.jdbc;

import com.google.code.arit.ResourceEnumerator;
import com.google.code.arit.ResourceEnumeratorFactory;

public class JdbcDriverEnumeratorFactory implements ResourceEnumeratorFactory {
    private final DriverManagerInspector driverManagerInspector;

    public JdbcDriverEnumeratorFactory(DriverManagerInspector driverManagerInspector) {
        this.driverManagerInspector = driverManagerInspector;
    }

    public ResourceEnumerator createEnumerator() {
        return new JdbcDriverEnumerator(driverManagerInspector.getDriverClasses());
    }
}
