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
package com.googlecode.arit.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.googlecode.arit.ModuleDescription;
import com.googlecode.arit.ModuleInspector;
import com.googlecode.arit.ModuleType;
import com.googlecode.arit.ResourceEnumerator;
import com.googlecode.arit.ResourceEnumeratorFactory;
import com.googlecode.arit.ResourceType;
import com.googlecode.arit.servlet.ModuleInspectorFactory;
import com.googlecode.arit.servlet.ModuleTypeIconManager;
import com.googlecode.arit.servlet.ResourceTypeIconManager;

@ManagedResource(objectName="com.googlecode.arit:type=ReportGenerator", description="Generates Arit reports")
public class ReportGenerator implements InitializingBean, DisposableBean {
    @Autowired
    private ModuleInspectorFactory moduleInspectorFactory;
    
    @Autowired
    private Set<ResourceEnumeratorFactory<?>> resourceEnumeratorFactories;
    
    @Autowired
    private ClassLoaderIdProvider classLoaderIdProvider;
    
    @Autowired
    private ResourceIdProvider resourceIdProvider;
    
    @Autowired
    @Qualifier("unknown")
    private ModuleType unknownModuleType;
    
    @Autowired
    private ModuleTypeIconManager moduleTypeIconManager;
    
//    @Requirement(role=IconManager.class, hint="identity")
//    private ModuleTypeIconManager moduleIdentityTypeIconManager;
    
    @Autowired
    private ModuleIdentityProvider moduleIdentityProvider;
    
    @Autowired
    private ResourceTypeIconManager resourceTypeIconManager;
    
    private List<ResourceEnumeratorFactory<?>> availableResourceEnumeratorFactories = new ArrayList<ResourceEnumeratorFactory<?>>();
    private List<ResourceEnumeratorFactory<?>> unavailableResourceEnumeratorFactories = new ArrayList<ResourceEnumeratorFactory<?>>();
    
    public void afterPropertiesSet() throws Exception {
        for (ResourceEnumeratorFactory<?> resourceEnumeratorFactory : resourceEnumeratorFactories) {
            if (resourceEnumeratorFactory.isAvailable()) {
                availableResourceEnumeratorFactories.add(resourceEnumeratorFactory);
            } else {
                unavailableResourceEnumeratorFactories.add(resourceEnumeratorFactory);
            }
        }
    }
    
    public void destroy() throws Exception {
        availableResourceEnumeratorFactories.clear();
        unavailableResourceEnumeratorFactories.clear();
    }

    public boolean isAvailable() {
        return moduleInspectorFactory.isAvailable();
    }
    
    @ManagedOperation(description="Generate an Arit report")
    public Report generateReport() {
        return generateReport(false);
    }
    
    public Report generateReport(boolean leaksOnly) {
        List<Message> messages = new ArrayList<Message>();
        List<Module> rootModules = new ArrayList<Module>();
//        ThreadLocalLogger.setTarget(messages);
        try {
            ModuleInspector moduleInspector = moduleInspectorFactory.createModuleInspector();
            ModuleHelper moduleHelper = new ModuleHelper(moduleInspector, classLoaderIdProvider, unknownModuleType, moduleTypeIconManager, moduleIdentityProvider);
            
            // If the application server supports this, load the entire module list before starting
            // to inspect the resources.
            List<ModuleDescription> moduleDescriptions = moduleInspector.listModules();
            if (moduleDescriptions != null) {
                for (ModuleDescription desc : moduleDescriptions) {
                    moduleHelper.getModule(desc);
                }
            }

            // A resource has class loader references to multiple class loaders and therefore to multiple
            // modules. In this case the report contains several Resource instances for a single resource.
            // This map is used to keep track of these instances.
            Map<Module,Resource> resourceMap = new HashMap<Module,Resource>();
            for (ResourceEnumeratorFactory<?> resourceEnumeratorFactory : availableResourceEnumeratorFactories) {
                ResourceEnumerator resourceEnumerator = resourceEnumeratorFactory.createEnumerator();
                while (resourceEnumerator.nextResource()) {
                    resourceMap.clear();
                    String resourceDescription = null;
                    Integer resourceId = null;
                    while (resourceEnumerator.nextClassLoaderReference()) {
                        ClassLoader classLoader = resourceEnumerator.getReferencedClassLoader();
                        if (classLoader != null) { // TODO: do we really need this check??
                            ModuleInfo moduleInfo = moduleHelper.getModule(classLoader);
                            if (moduleInfo != null) {
                                Module module = moduleInfo.getModule();
                                Resource resource = resourceMap.get(module);
                                if (resource == null) {
                                    ResourceType resourceType = resourceEnumerator.getResourceType();
                                    if (resourceId == null) {
                                        resourceId = resourceIdProvider.getResourceId(resourceType.getIdentifier(), resourceEnumerator.getResourceObject(), true);
                                    }
                                    if (resourceDescription == null) {
                                        resourceDescription = resourceEnumerator.getResourceDescription(moduleInfo);
                                        if (resourceType.isShowResourceId()) {
                                            resourceDescription = resourceDescription + " (" + resourceId + ")";
                                        }
                                    }
                                    resource = new Resource(resourceId, resourceTypeIconManager.getIcon(resourceType).getIconImage("default").getFileName(), resourceType.getIdentifier(), resourceDescription);
                                    module.getResources().add(resource);
                                    resourceMap.put(module, resource);
                                }
                                resource.getLinks().add(new ClassLoaderLink(resourceEnumerator.getClassLoaderReferenceDescription(moduleInfo)));
                            }
                        }
                    }
                }
            }
            for (Module module : moduleHelper.getModules()) {
                if (module != null && module.getParent() == null && (!leaksOnly || module.isStopped())) {
                    rootModules.add(module);
                }
            }
        } finally {
//            ThreadLocalLogger.setTarget(null);
        }
        Collections.sort(rootModules, new Comparator<Module>() {
            public int compare(Module o1, Module o2) {
                String name1 = o1.getName();
                String name2 = o2.getName();
                if (name1 == null && name2 == null) {
                    return 0;
                } else if (name1 == null) {
                    return -1;
                } else if (name2 == null) {
                    return 1;
                } else {
                    int c = name1.compareTo(name2);
                    return c != 0 ? c : o1.getId().compareTo(o2.getId());
                }
            }
        });
        return new Report(messages, rootModules);
    }

    public List<ResourceEnumeratorFactory<?>> getAvailableResourceEnumeratorFactories() {
        return Collections.unmodifiableList(availableResourceEnumeratorFactories);
    }
}
