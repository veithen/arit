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

import org.codehaus.plexus.component.annotations.Requirement;

import com.googlecode.arit.ModuleDescription;
import com.googlecode.arit.ModuleInspector;
import com.googlecode.arit.ModuleType;
import com.googlecode.arit.rbeans.RBeanFactory;

public class WASModuleInspector implements ModuleInspector {
    private final RBeanFactory rbf;
    private final Map<ClassLoader,ModuleDescription> moduleMap;
    private final ModuleType earModuleType;
    private final ModuleType ejbJarModuleType;
    private final ModuleType warModuleType;

    public WASModuleInspector(RBeanFactory rbf,
            Map<ClassLoader, ModuleDescription> moduleMap,
            ModuleType earModuleType, ModuleType ejbJarModuleType,
            ModuleType warModuleType) {
        this.rbf = rbf;
        this.moduleMap = moduleMap;
        this.earModuleType = earModuleType;
        this.ejbJarModuleType = ejbJarModuleType;
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
            String name = rbf.createRBean(CompoundClassLoaderRBean.class, classLoader).getName();
            if (name.startsWith("app:")) {
                return new ModuleDescription(earModuleType, name.substring(4), classLoader);
            } else if (name.startsWith("war:")) {
                return new ModuleDescription(warModuleType, name.substring(name.lastIndexOf('/')+1), classLoader);
            } else {
                return new ModuleDescription(null, name, classLoader);
            }
        } else {
            return null;
        }
    }
}
