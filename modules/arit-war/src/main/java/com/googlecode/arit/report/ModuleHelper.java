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

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import com.googlecode.arit.ModuleDescription;
import com.googlecode.arit.ModuleIdentity;
import com.googlecode.arit.ModuleInspector;
import com.googlecode.arit.ModuleStatus;
import com.googlecode.arit.ModuleType;
import com.googlecode.arit.servlet.ModuleTypeIconManager;

public class ModuleHelper {
    private final ModuleInspector moduleInspector;
    private final ClassLoaderIdProvider classLoaderIdProvider;
    private final ModuleType unknownModuleType;
    private final ModuleTypeIconManager moduleTypeIconManager;
    private final ModuleIdentityProvider moduleIdentityProvider;
    private final Map<ClassLoader,Module> moduleMap = new IdentityHashMap<ClassLoader,Module>();
    
    public ModuleHelper(ModuleInspector moduleInspector,
            ClassLoaderIdProvider classLoaderIdProvider,
            ModuleType unknownModuleType,
            ModuleTypeIconManager moduleTypeIconManager,
            ModuleIdentityProvider moduleIdentityProvider) {
        this.moduleInspector = moduleInspector;
        this.classLoaderIdProvider = classLoaderIdProvider;
        this.unknownModuleType = unknownModuleType;
        this.moduleTypeIconManager = moduleTypeIconManager;
        this.moduleIdentityProvider = moduleIdentityProvider;
    }

    public Module getModule(ClassLoader classLoader) {
        if (moduleMap.containsKey(classLoader)) {
            return moduleMap.get(classLoader);
        } else {
            ModuleDescription desc = moduleInspector.inspect(classLoader);
            if (desc == null) {
                Module module;
                // There may be intermediary class loaders that we don't identify as modules. Therefore
                // multiple class loaders may correspond to a single module and we need to walk up the
                // class loader hierarchy until we identify a module.
                ClassLoader parentClassLoader = classLoader.getParent();
                if (parentClassLoader != null) {
                    module = getModule(parentClassLoader);
                } else {
                    module = null;
                }
                // Cache the result
                moduleMap.put(classLoader, module);
                return module;
            } else {
                return loadModule(desc);
            }
        }
    }
    
    public Module getModule(ModuleDescription desc) {
        ClassLoader classLoader = desc.getClassLoader();
        if (moduleMap.containsKey(classLoader)) {
            return moduleMap.get(classLoader);
        } else {
            return loadModule(desc);
        }
    }
    
    public Set<Module> getModules() {
        Set<Module> result = new HashSet<Module>();
        for (Module module : moduleMap.values()) {
            if (module != null) {
                result.add(module);
            }
        }
        return result;
    }
    
    private Module loadModule(ModuleDescription desc) {
        ClassLoader classLoader = desc.getClassLoader();
        Module module = new Module(classLoaderIdProvider.getClassLoaderId(classLoader, true), desc.getDisplayName(), desc.getStatus() == ModuleStatus.STOPPED);
        ModuleType moduleType = desc.getType();
        Module parentModule;
        ClassLoader parentClassLoader = classLoader.getParent();
        if (parentClassLoader != null) {
            parentModule = getModule(parentClassLoader);
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
        for (ModuleIdentity identity : moduleIdentityProvider.getModuleIdentities(desc.getUrl(), classLoader)) {
            module.addIdentity(new Identity(identity.getType().getName(), identity.getValue()));
        }
        moduleMap.put(classLoader, module);
        return module;
    }
}
