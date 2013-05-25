/*
 * Copyright 2010-2011 Andreas Veithen
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
package com.googlecode.arit.websphere;

import javax.management.MBeanServer;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.veithen.rbeans.RBeanFactory;
import com.github.veithen.rbeans.RBeanFactoryException;
import com.googlecode.arit.mbeans.MBeanAccessor;
import com.googlecode.arit.mbeans.MBeanServerInspectorPlugin;
import com.googlecode.arit.mbeans.sun.java5.SunJava5MBeanServerInspectorPlugin;
import com.googlecode.arit.mbeans.sun.java6.SunJava6MBeanServerInspectorPlugin;

public class WASMBeanServerInspectorPlugin implements MBeanServerInspectorPlugin {
    private final RBeanFactory rbf;
    
    // TODO: we can't have a requirment on MBeanServerInspector because this would cause a cyclic dependency; maybe it's possible to do this with Initializable
    @Autowired
    private SunJava5MBeanServerInspectorPlugin sunJava5MbsInspector;
    
    @Autowired
    private SunJava6MBeanServerInspectorPlugin sunJava6MbsInspector;
    
    public WASMBeanServerInspectorPlugin() {
        RBeanFactory rbf;
        try {
            rbf = new RBeanFactory(PlatformMBeanServerRBean.class);
        } catch (RBeanFactoryException ex) {
            rbf = null;
        }
        this.rbf = rbf;
    }
    
    public boolean isAvailable() {
        // TODO: we should issue a warning if we are able to create the RBeanFactory, but no MBeanServerInspector is available
        return rbf != null && (sunJava5MbsInspector.isAvailable() || sunJava6MbsInspector.isAvailable());
    }

    public MBeanAccessor inspect(MBeanServer mbs) {
        if (rbf.getRBeanInfo(PlatformMBeanServerRBean.class).getTargetClass().isInstance(mbs)) {
            MBeanServerInspectorPlugin mbsInspector;
            if (sunJava5MbsInspector.isAvailable()) {
                mbsInspector = sunJava5MbsInspector;
            } else {
                mbsInspector = sunJava6MbsInspector;
            }
            return mbsInspector.inspect(rbf.createRBean(PlatformMBeanServerRBean.class, mbs).getDefaultMBeanServer());
        } else {
            return null;
        }
    }
}
