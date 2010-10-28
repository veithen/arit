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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
    
    @Requirement(hint="appwar")
    private ModuleType appWarModuleType;
    
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
            jmxNameMap.put(warModuleType, new ObjectName("WebSphere:type=WebModule,*"));
        } catch (MalformedObjectNameException ex) {
            throw new InitializationException("Failed to create object name", ex);
        }
    }

    public boolean isAvailable() {
        return rbf != null && mbeanAccessor != null;
    }
    
    private List<DeployedObjectCollaboratorRBean> getCollaborators(ObjectName query) {
        Set<ObjectName> names = mbs.queryNames(query, null);
        List<DeployedObjectCollaboratorRBean> collaborators = new ArrayList<DeployedObjectCollaboratorRBean>(names.size());
        for (ObjectName name : names) {
            collaborators.add(rbf.createRBean(DeployedObjectCollaboratorRBean.class, mbeanAccessor.retrieve(name))); 
        }
        return collaborators;
    }
    
    public ModuleInspector createModuleInspector() {
        List<DeployedObjectCollaboratorRBean> earCollaborators;
        List<DeployedObjectCollaboratorRBean> warCollaborators;
        try {
            earCollaborators = getCollaborators(new ObjectName("WebSphere:type=Application,*"));
            warCollaborators = getCollaborators(new ObjectName("WebSphere:type=WebModule,*"));
        } catch (MalformedObjectNameException ex) {
            throw new Error("Failed to create object name", ex);
        }
        
        // In WebSphere it is possible to configure an application with a common class loader for all
        // WARs. We need to identify these cases and represent them using a distinct module type.
        Set<ClassLoader> earClassLoaders = new HashSet<ClassLoader>();
        for (DeployedObjectCollaboratorRBean collaborator : earCollaborators) {
            earClassLoaders.add(collaborator.getDeployedObject().getClassLoader());
        }
        Set<ClassLoader> appWarClassLoaders = new HashSet<ClassLoader>();
        for (DeployedObjectCollaboratorRBean collaborator : warCollaborators) {
            ClassLoader classLoader = collaborator.getDeployedObject().getClassLoader();
            if (earClassLoaders.contains(classLoader)) {
                appWarClassLoaders.add(classLoader);
            }
        }
        
        Map<ClassLoader,ModuleDescription> moduleMap = new HashMap<ClassLoader,ModuleDescription>();
        for (DeployedObjectCollaboratorRBean collaborator : earCollaborators) {
            DeployedObjectRBean deployedObject = collaborator.getDeployedObject();
            ClassLoader classLoader = deployedObject.getClassLoader();
            URL url = null;
            if (deployedObject instanceof DeployedApplicationRBean) {
                String dir = ((DeployedApplicationRBean)deployedObject).getBinariesURL();
                // The getBinariesURL method doesn't exist on WAS 6.1
                if (dir != null) {
                    try {
                        url = new File(dir).toURL();
                    } catch (MalformedURLException ex) {
                        // Just continue
                    }
                }
            }
            moduleMap.put(classLoader, new ModuleDescription(appWarClassLoaders.contains(classLoader) ? appWarModuleType : earModuleType, collaborator.getName(), classLoader, url, ModuleStatus.STARTED));
        }
        for (DeployedObjectCollaboratorRBean collaborator : warCollaborators) {
            ClassLoader classLoader = collaborator.getDeployedObject().getClassLoader();
            if (!appWarClassLoaders.contains(classLoader)) {
                moduleMap.put(classLoader, new ModuleDescription(warModuleType, collaborator.getName(), classLoader, null, ModuleStatus.STARTED));
            }
        }
        
        return new WASModuleInspector(rbf, moduleMap, earModuleType, appWarModuleType, warModuleType);
    }
}
