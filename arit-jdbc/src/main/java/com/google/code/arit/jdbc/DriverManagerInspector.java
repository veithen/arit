package com.google.code.arit.jdbc;

import java.util.List;

public interface DriverManagerInspector {
    List<Class<?>> getDriverClasses();
}
