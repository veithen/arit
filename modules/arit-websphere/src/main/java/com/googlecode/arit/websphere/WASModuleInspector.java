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

import java.util.List;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.codehaus.plexus.logging.Logger;

import com.googlecode.arit.ModuleDescription;
import com.googlecode.arit.ModuleInspector;
import com.googlecode.arit.mbeans.MBeanRepository;
import com.googlecode.arit.mbeans.MBeanServerInspector;
import com.googlecode.arit.rbeans.RBeanFactory;

public class WASModuleInspector implements ModuleInspector {
    private final RBeanFactory rbf;
    private final MBeanServerInspector mbsInspector;
    private final Logger log;
    
    public WASModuleInspector(RBeanFactory rbf, MBeanServerInspector mbsInspector, Logger log) {
        this.rbf = rbf;
        this.mbsInspector = mbsInspector;
        this.log = log;
    }

    public List<ModuleDescription> listModules() {
        // TODO: it should be sufficient to get the MBeanServer only once, not for every call to listModules
        MBeanServer mbs = rbf.createRBean(AdminServiceFactoryRBean.class).getMBeanFactory().getMBeanServer();
        MBeanRepository repository = mbsInspector.inspect(mbs);
        if (repository == null) {
            log.error("Unable to inspect WebSphere's MBean server; this is unexpected because we are in a WebSphere specific plugin");
            return null;
        } else {
            Set<ObjectName> names;
            try {
                names = mbs.queryNames(new ObjectName("WebSphere:type=Application,*"), null);
            } catch (MalformedObjectNameException ex) {
                log.fatalError("Failed to create object name", ex);
                return null;
            }
            for (ObjectName name : names) {
                DeployedObjectRBean deployedObject = rbf.createRBean(DeployedObjectCollaboratorRBean.class, repository.retrieve(name)).getDeployedObject();
                log.info(deployedObject.getClassLoader().toString());
            }
            // TODO
            return null;
        }
    }

    public ModuleDescription inspect(ClassLoader classLoader) {
        if (rbf.getRBeanInfo(CompoundClassLoaderRBean.class).getTargetClass().isInstance(classLoader)) {
            return new ModuleDescription(null, rbf.createRBean(CompoundClassLoaderRBean.class, classLoader).getName(), classLoader);
        } else {
            return null;
        }
    }
}
