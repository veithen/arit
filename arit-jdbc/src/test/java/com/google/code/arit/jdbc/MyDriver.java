package com.google.code.arit.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;

public class MyDriver implements Driver {
    public boolean acceptsURL(String url) throws SQLException {
        return false;
    }

    public Connection connect(String url, Properties info) throws SQLException {
        return null;
    }

    public int getMajorVersion() {
        return 1;
    }

    public int getMinorVersion() {
        return 0;
    }

    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return null;
    }

    public boolean jdbcCompliant() {
        return true;
    }
}
