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
package com.googlecode.arit.websphere;

import javax.management.MBeanServer;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

import com.googlecode.arit.mbeans.MBeanAccessor;
import com.googlecode.arit.mbeans.MBeanServerInspectorPlugin;
import com.googlecode.arit.rbeans.RBeanFactory;
import com.googlecode.arit.rbeans.RBeanFactoryException;

@Component(role=MBeanServerInspectorPlugin.class, hint="websphere")
public class WASMBeanServerInspectorPlugin implements MBeanServerInspectorPlugin {
    private final RBeanFactory rbf;
    
    @Requirement(hint="sun-java6")
    private MBeanServerInspectorPlugin sunMbsInspector;
    
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
        return rbf != null;
    }

    public MBeanAccessor inspect(MBeanServer mbs) {
        if (rbf.getRBeanInfo(PlatformMBeanServerRBean.class).getTargetClass().isInstance(mbs)) {
            return sunMbsInspector.inspect(rbf.createRBean(PlatformMBeanServerRBean.class, mbs).getDefaultMBeanServer());
        } else {
            return null;
        }
    }
}
