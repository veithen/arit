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
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Disposable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import com.googlecode.arit.ModuleDescription;
import com.googlecode.arit.ModuleInspector;
import com.googlecode.arit.ModuleStatus;
import com.googlecode.arit.ModuleType;
import com.googlecode.arit.ResourceEnumerator;
import com.googlecode.arit.ResourceEnumeratorFactory;
import com.googlecode.arit.icon.IconManager;
import com.googlecode.arit.servlet.ModuleInspectorFactory;
import com.googlecode.arit.servlet.ModuleTypeIconManager;
import com.googlecode.arit.servlet.ResourceTypeIconManager;
import com.googlecode.arit.servlet.log.ThreadLocalLogger;

@Component(role=ReportGenerator.class)
public class ReportGenerator implements Initializable, Disposable {
    @Requirement
    private ModuleInspectorFactory moduleInspectorFactory;
    
    @Requirement(role=ResourceEnumeratorFactory.class)
    private List<ResourceEnumeratorFactory> resourceEnumeratorFactories;
    
    @Requirement
    private ClassLoaderIdProvider classLoaderIdProvider;
    
    @Requirement(hint="unknown")
    private ModuleType unknownModuleType;
    
    @Requirement(role=IconManager.class, hint="module")
    private ModuleTypeIconManager moduleTypeIconManager;
    
    @Requirement
    private ModuleIdentityProvider moduleIdentityProvider;
    
    @Requirement(role=IconManager.class, hint="resource")
    private ResourceTypeIconManager resourceTypeIconManager;
    
    private List<ResourceEnumeratorFactory> availableResourceEnumeratorFactories = new ArrayList<ResourceEnumeratorFactory>();
    private List<ResourceEnumeratorFactory> unavailableResourceEnumeratorFactories = new ArrayList<ResourceEnumeratorFactory>();
    
    public void initialize() throws InitializationException {
        for (ResourceEnumeratorFactory resourceEnumeratorFactory : resourceEnumeratorFactories) {
            if (resourceEnumeratorFactory.isAvailable()) {
                availableResourceEnumeratorFactories.add(resourceEnumeratorFactory);
            } else {
                unavailableResourceEnumeratorFactories.add(resourceEnumeratorFactory);
            }
        }
    }
    
    public void dispose() {
        availableResourceEnumeratorFactories.clear();
        unavailableResourceEnumeratorFactories.clear();
    }

    public boolean isAvailable() {
        return moduleInspectorFactory.isAvailable();
    }
    
    private Module getModule(ModuleInspector moduleInspector, Map<ClassLoader,Module> moduleMap, ClassLoader classLoader) {
        if (moduleMap.containsKey(classLoader)) {
            return moduleMap.get(classLoader);
        } else {
            ModuleDescription desc = moduleInspector.inspect(classLoader);
            if (desc == null) {
                return null;
            } else {
                return loadModule(moduleInspector, moduleMap, desc);
            }
        }
    }
    
    private Module getModule(ModuleInspector moduleInspector, Map<ClassLoader,Module> moduleMap, ModuleDescription desc) {
        ClassLoader classLoader = desc.getClassLoader();
        if (moduleMap.containsKey(classLoader)) {
            return moduleMap.get(classLoader);
        } else {
            return loadModule(moduleInspector, moduleMap, desc);
        }
    }
    
    // TODO: refactor the get/loadModule stuff into a ReportBuilder class and make this a private method of the class (it must only be called by getModule)
    private Module loadModule(ModuleInspector moduleInspector, Map<ClassLoader,Module> moduleMap, ModuleDescription desc) {
        ClassLoader classLoader = desc.getClassLoader();
        Module module = new Module(classLoaderIdProvider.getClassLoaderId(classLoader), desc.getDisplayName(), desc.getStatus() == ModuleStatus.STOPPED);
        ModuleType moduleType = desc.getType();
        ClassLoader parentClassLoader = classLoader.getParent();
        Module parentModule;
        if (parentClassLoader != null) {
            // TODO: we should actually walk up the hierarchy until we identify a class loader
            parentModule = getModule(moduleInspector, moduleMap, parentClassLoader);
            if (parentModule != null) {
                parentModule.addChild(module);
            }
        } else {
            parentModule = null;
        }
        String variant;
        if (desc.getStatus() == ModuleStatus.STOPPED) {
            if (parentModule == null || !parentModule.isStopped()) {
                variant = "defunct";
            } else {
                variant = "grayed";
            }
        } else {
            variant = "default";
        }
        module.setIcon(moduleTypeIconManager.getIcon(moduleType == null ? unknownModuleType : moduleType).getIconImage(variant).getFileName());
        module.setIdentities(moduleIdentityProvider.getModuleIdentities(desc.getUrl(), classLoader));
        moduleMap.put(classLoader, module);
        return module;
    }

    public Report generateReport() {
        List<Message> messages = new ArrayList<Message>();
        List<Module> rootModules = new ArrayList<Module>();
        ThreadLocalLogger.setTarget(messages);
        try {
            ModuleInspector moduleInspector = moduleInspectorFactory.createModuleInspector();
            Map<ClassLoader,Module> moduleMap = new IdentityHashMap<ClassLoader,Module>();
            
            // If the application server supports this, load the entire module list before starting
            // to inspect the resources.
            List<ModuleDescription> moduleDescriptions = moduleInspector.listModules();
            if (moduleDescriptions != null) {
                for (ModuleDescription desc : moduleDescriptions) {
                    getModule(moduleInspector, moduleMap, desc);
                }
            }
            
            for (ResourceEnumeratorFactory resourceEnumeratorFactory : availableResourceEnumeratorFactories) {
                ResourceEnumerator resourceEnumerator = resourceEnumeratorFactory.createEnumerator();
                while (resourceEnumerator.next()) {
                    for (ClassLoader classLoader : resourceEnumerator.getClassLoaders()) {
                        if (classLoader != null) {
                            Module module = getModule(moduleInspector, moduleMap, classLoader);
                            // TODO: we should actually walk up the hierarchy until we identify a class loader (because an application may create its own class loaders)
                            if (module != null) {
                                module.getResources().add(new Resource(resourceTypeIconManager.getIcon(resourceEnumerator.getType()).getIconImage("default").getFileName(), resourceEnumerator.getDescription()));
                                break;
                            }
                        }
                    }
                }
            }
            for (Module module : moduleMap.values()) {
                if (module != null && module.getParent() == null) {
                    rootModules.add(module);
                }
            }
        } finally {
            ThreadLocalLogger.setTarget(null);
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
                    return name1.compareTo(name2);
                }
            }
        });
        return new Report(messages, rootModules);
    }

    public List<ResourceEnumeratorFactory> getAvailableResourceEnumeratorFactories() {
        return Collections.unmodifiableList(availableResourceEnumeratorFactories);
    }
}
