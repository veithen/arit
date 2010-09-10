package com.googlecode.arit.jdbc;

import java.util.Vector;

import com.googlecode.arit.rbeans.Accessor;
import com.googlecode.arit.rbeans.RBean;

@RBean(target="java.sql.DriverManager", isStatic=true)
public interface SunDriverManagerRBean {
    // Java 1.5 uses "drivers" attribute.
    // Java 1.6 has some copy-on-write feature and uses "readDrivers".
    @Accessor(name={"drivers", "readDrivers"})
    Vector<?> getDrivers();
}
