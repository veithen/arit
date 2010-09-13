/*
 * Copyright 2010 Andreas Veithen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.arit.jdbc.ibm;

import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.component.annotations.Component;

import com.googlecode.arit.jdbc.DriverManagerInspectorPlugin;
import com.googlecode.arit.rbeans.RBeanFactory;
import com.googlecode.arit.rbeans.RBeanFactoryException;

@Component(role=DriverManagerInspectorPlugin.class, hint="ibm")
public class IBMDriverManagerInspectorPlugin implements DriverManagerInspectorPlugin {
    private final List<Driver> drivers;
    
    public IBMDriverManagerInspectorPlugin() {
        List<Driver> drivers;
        try {
            RBeanFactory rbf = new RBeanFactory(DriverManagerRBean.class);
            // "theDrivers" is a final field; we only need to retrieve it once
            DriverManagerRBean driverManager = rbf.createRBean(DriverManagerRBean.class);
            drivers = driverManager.getDrivers();
        } catch (RBeanFactoryException ex) {
            drivers = null;
        }
        this.drivers = drivers;
    }
    
    public boolean isAvailable() {
        return drivers != null;
    }

    public List<Class<?>> getDriverClasses() {
        List<Class<?>> driverClasses = new ArrayList<Class<?>>(drivers.size());
        for (Object driver : drivers) {
            driverClasses.add(driver.getClass());
        }
        return driverClasses;
    }
}
