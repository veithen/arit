package com.google.code.rex.jdbc;

import java.util.List;

public interface DriverManagerInspector {
    List<Class<?>> getDriverClasses();
}
