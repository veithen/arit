package com.googlecode.arit.jdbc.sun;

import com.googlecode.arit.rbeans.Accessor;
import com.googlecode.arit.rbeans.RBean;

@RBean(target="java.sql.DriverInfo")
public interface DriverInfoRBean {
    @Accessor(name="driverClass")
    Class<?> getDriverClass();
}
