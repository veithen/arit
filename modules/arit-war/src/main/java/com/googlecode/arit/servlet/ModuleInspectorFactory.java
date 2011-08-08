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
package com.googlecode.arit.servlet;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.arit.ModuleDescription;
import com.googlecode.arit.ModuleInspector;
import com.googlecode.arit.ModuleInspectorPlugin;
import com.googlecode.arit.PluginManager;

public class ModuleInspectorFactory extends PluginManager<ModuleInspectorPlugin> {
    public ModuleInspectorFactory() {
        super(ModuleInspectorPlugin.class);
    }
    
    public ModuleInspector createModuleInspector() {
        final List<ModuleInspector> inspectors = new ArrayList<ModuleInspector>();
        for (ModuleInspectorPlugin provider : getPlugins()) {
            ModuleInspector inspector = provider.createModuleInspector();
            if (inspector != null) {
                inspectors.add(inspector);
            }
        }
        if (inspectors.isEmpty()) {
            return null;
        } else if (inspectors.size() == 0) {
            return inspectors.get(0);
        } else {
            return new ModuleInspector() {
                public List<ModuleDescription> listModules() {
                    List<ModuleDescription> result = null;
                    for (ModuleInspector inspector : inspectors) {
                        List<ModuleDescription> moduleDescriptions = inspector.listModules();
                        if (moduleDescriptions != null) {
                            if (result == null) {
                                result = new ArrayList<ModuleDescription>(inspector.listModules());
                            } else {
                                result.addAll(inspector.listModules());
                            }
                        }
                    }
                    return result;
                }
                
                public ModuleDescription inspect(ClassLoader classLoader) {
                    for (ModuleInspector inspector : inspectors) {
                        ModuleDescription moduleDescription = inspector.inspect(classLoader);
                        if (moduleDescription != null) {
                            return moduleDescription;
                        }
                    }
                    return null;
                }
            };
        }
    }
}
