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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import com.googlecode.arit.ModuleDescription;
import com.googlecode.arit.ModuleInspector;
import com.googlecode.arit.ModuleInspectorPlugin;
import com.googlecode.arit.ModuleStatus;
import com.googlecode.arit.ModuleType;
import com.googlecode.arit.mbeans.MBeanAccessor;
import com.googlecode.arit.mbeans.MBeanServerInspector;
import com.googlecode.arit.rbeans.RBeanFactory;
import com.googlecode.arit.rbeans.RBeanFactoryException;

@Component(role=ModuleInspectorPlugin.class, hint="websphere")
public class WASModuleInspectorPlugin implements ModuleInspectorPlugin, Initializable {
    @Requirement
    private MBeanServerInspector mbsInspector;
    
    @Requirement(hint="ear")
    private ModuleType earModuleType;
    
    @Requirement(hint="ejb-jar")
    private ModuleType ejbJarModuleType;
    
    @Requirement(hint="war")
    private ModuleType warModuleType;
    
    private RBeanFactory rbf;
    private MBeanServer mbs;
    private MBeanAccessor mbeanAccessor;
    private Map<ModuleType,ObjectName> jmxNameMap = new HashMap<ModuleType,ObjectName>();
    
    public void initialize() throws InitializationException {
        try {
            rbf = new RBeanFactory(AdminServiceFactoryRBean.class, DeployedObjectCollaboratorRBean.class, CompoundClassLoaderRBean.class);
        } catch (RBeanFactoryException ex) {
            return;
        }
        mbs = rbf.createRBean(AdminServiceFactoryRBean.class).getMBeanFactory().getMBeanServer();
        mbeanAccessor = mbsInspector.inspect(mbs);
        if (mbeanAccessor == null) {
            throw new InitializationException("Unable to inspect WebSphere's MBean server; this is unexpected because we are in a WebSphere specific plugin");
        }
        try {
            jmxNameMap.put(earModuleType, new ObjectName("WebSphere:type=Application,*"));
            jmxNameMap.put(ejbJarModuleType, new ObjectName("WebSphere:type=EJBModule,*"));
            jmxNameMap.put(warModuleType, new ObjectName("WebSphere:type=WebModule,*"));
        } catch (MalformedObjectNameException ex) {
            throw new InitializationException("Failed to create object name", ex);
        }
    }

    public boolean isAvailable() {
        return rbf != null && mbeanAccessor != null;
    }

    public ModuleInspector createModuleInspector() {
        Map<ClassLoader,ModuleDescription> moduleMap = new HashMap<ClassLoader,ModuleDescription>();
        for (Map.Entry<ModuleType,ObjectName> entry : jmxNameMap.entrySet()) {
            Set<ObjectName> names = mbs.queryNames(entry.getValue(), null);
            for (ObjectName name : names) {
                DeployedObjectCollaboratorRBean collaborator = rbf.createRBean(DeployedObjectCollaboratorRBean.class, mbeanAccessor.retrieve(name)); 
                DeployedObjectRBean deployedObject = collaborator.getDeployedObject();
                ClassLoader classLoader = deployedObject.getClassLoader();
                moduleMap.put(classLoader, new ModuleDescription(entry.getKey(), collaborator.getName(), classLoader, ModuleStatus.STARTED));
            }
        }
        return new WASModuleInspector(rbf, moduleMap, earModuleType, ejbJarModuleType, warModuleType);
    }
}
