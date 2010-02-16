package com.google.code.rex.jdbc;

import com.google.code.rex.ResourceEnumerator;
import com.google.code.rex.ResourceEnumeratorFactory;

public class JdbcDriverEnumeratorFactory implements ResourceEnumeratorFactory {
    private final DriverManagerInspector driverManagerInspector;

    public JdbcDriverEnumeratorFactory(DriverManagerInspector driverManagerInspector) {
        this.driverManagerInspector = driverManagerInspector;
    }

    public ResourceEnumerator createEnumerator() {
        return new JdbcDriverEnumerator(driverManagerInspector.getDriverClasses());
    }
}
