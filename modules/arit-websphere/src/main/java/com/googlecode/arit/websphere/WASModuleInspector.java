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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.googlecode.arit.ModuleDescription;
import com.googlecode.arit.ModuleInspector;
import com.googlecode.arit.ModuleStatus;
import com.googlecode.arit.ModuleType;
import com.googlecode.arit.rbeans.RBeanFactory;

public class WASModuleInspector implements ModuleInspector {
    private final RBeanFactory rbf;
    private final Map<ClassLoader,ModuleDescription> moduleMap;
    private final ModuleType earModuleType;
    private final ModuleType appWarModuleType;
    private final ModuleType warModuleType;

    public WASModuleInspector(RBeanFactory rbf,
            Map<ClassLoader, ModuleDescription> moduleMap,
            ModuleType earModuleType, ModuleType appWarModuleType,
            ModuleType warModuleType) {
        this.rbf = rbf;
        this.moduleMap = moduleMap;
        this.earModuleType = earModuleType;
        this.appWarModuleType = appWarModuleType;
        this.warModuleType = warModuleType;
    }

    public List<ModuleDescription> listModules() {
        return new ArrayList<ModuleDescription>(moduleMap.values());
    }

    public ModuleDescription inspect(ClassLoader classLoader) {
        ModuleDescription desc = moduleMap.get(classLoader);
        if (desc != null) {
            return desc;
        } else if (rbf.getRBeanInfo(CompoundClassLoaderRBean.class).getTargetClass().isInstance(classLoader)) {
            CompoundClassLoaderRBean ccl = rbf.createRBean(CompoundClassLoaderRBean.class, classLoader);
            String name = ccl.getName();
            ModuleType moduleType;
            String moduleName;
            ModuleStatus moduleStatus;
            if (name == null) {
                // This case occurs on WAS 6, which doesn't have a "name" field in CompoundClassLoader
                moduleType = null;
                // TODO: this should ultimately also be indicated as a null value; however, it is not sure how the rest of the application will react
                moduleName = "<unknown>";
                moduleStatus = ModuleStatus.UNKNOWN;
                for (String path : ccl.getPaths()) {
                    path = path.replace('\\', '/');
                    if (path.endsWith("/WEB-INF/classes")) {
                        moduleType = warModuleType;
                        moduleName = path.substring(path.lastIndexOf('/', path.length()-17)+1, path.length()-16);
                        moduleStatus = ModuleStatus.STOPPED;
                        break;
                    }
                }
            } else if (name.startsWith("app:")) {
                moduleType = earModuleType;
                moduleName = name.substring(4);
                moduleStatus = ModuleStatus.STOPPED;
            } else if (name.startsWith("appwar:")) {
                moduleType = appWarModuleType;
                moduleName = name.substring(7);
                moduleStatus = ModuleStatus.STOPPED;
            } else if (name.startsWith("war:")) {
                moduleType = warModuleType;
                moduleName = name.substring(name.lastIndexOf('/')+1);
                moduleStatus = ModuleStatus.STOPPED;
            } else {
                moduleType = null;
                moduleName = name;
                moduleStatus = ModuleStatus.UNKNOWN;
            }
            return new ModuleDescription(moduleType, moduleName, classLoader, null, moduleStatus);
        } else {
            return null;
        }
    }
}
