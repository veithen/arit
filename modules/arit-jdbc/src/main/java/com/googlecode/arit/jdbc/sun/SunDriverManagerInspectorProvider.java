package com.googlecode.arit.jdbc.sun;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.googlecode.arit.Provider;
import com.googlecode.arit.jdbc.DriverManagerInspector;
import com.googlecode.arit.rbeans.RBeanFactory;
import com.googlecode.arit.rbeans.RBeanFactoryException;

public class SunDriverManagerInspectorProvider implements Provider<DriverManagerInspector> {
    public DriverManagerInspector getImplementation() {
        try {
            final RBeanFactory rbf = new RBeanFactory(DriverManagerRBean.class, DriverInfoRBean.class);
            final DriverManagerRBean driverManager = rbf.createRBean(DriverManagerRBean.class);
            return new DriverManagerInspector() {
                public List<Class<?>> getDriverClasses() {
                    // We need to get the field value every time, because in JRE 1.6, the Vector
                    // is replaced when a new driver is added.
                    Vector<?> drivers = driverManager.getDrivers();
                    List<Class<?>> driverClasses = new ArrayList<Class<?>>(drivers.size());
                    for (Object driverInfo : drivers) {
                        driverClasses.add(rbf.createRBean(DriverInfoRBean.class, driverInfo).getDriverClass());
                    }
                    return driverClasses;
                }
            };
        } catch (RBeanFactoryException ex) {
            return null;
        }
    }
}
