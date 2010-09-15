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

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;

import com.googlecode.arit.ModuleInspector;
import com.googlecode.arit.ModuleInspectorPlugin;
import com.googlecode.arit.mbeans.MBeanServerInspector;
import com.googlecode.arit.rbeans.RBeanFactory;
import com.googlecode.arit.rbeans.RBeanFactoryException;

@Component(role=ModuleInspectorPlugin.class, hint="websphere")
public class WASModuleInspectorPlugin implements ModuleInspectorPlugin {
    private final RBeanFactory rbf;
    
    @Requirement
    private MBeanServerInspector mbsInspector;
    
    @Requirement
    private Logger log;
    
    public WASModuleInspectorPlugin() {
        RBeanFactory rbf;
        try {
            rbf = new RBeanFactory(AdminServiceFactoryRBean.class, DeployedObjectCollaboratorRBean.class, CompoundClassLoaderRBean.class);
        } catch (RBeanFactoryException ex) {
            rbf = null;
        }
        this.rbf = rbf;
    }

    public boolean isAvailable() {
        return rbf != null;
    }

    public ModuleInspector createModuleInspector() {
        return new WASModuleInspector(rbf, mbsInspector, log);
    }
}
