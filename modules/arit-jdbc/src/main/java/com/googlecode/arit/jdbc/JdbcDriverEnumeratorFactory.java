package com.googlecode.arit.jdbc;

import com.googlecode.arit.ResourceEnumerator;
import com.googlecode.arit.ResourceEnumeratorFactory;

public class JdbcDriverEnumeratorFactory implements ResourceEnumeratorFactory {
    private final DriverManagerInspector driverManagerInspector;

    public JdbcDriverEnumeratorFactory(DriverManagerInspector driverManagerInspector) {
        this.driverManagerInspector = driverManagerInspector;
    }

    public String getDescription() {
        return "JDBC drivers";
    }

    public ResourceEnumerator createEnumerator() {
        return new JdbcDriverEnumerator(driverManagerInspector.getDriverClasses());
    }
}
