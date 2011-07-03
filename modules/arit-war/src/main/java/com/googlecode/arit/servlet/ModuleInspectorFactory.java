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

import com.googlecode.arit.ModuleInspector;
import com.googlecode.arit.ModuleInspectorPlugin;
import com.googlecode.arit.PluginManager;

public class ModuleInspectorFactory extends PluginManager<ModuleInspectorPlugin> {
    public ModuleInspectorFactory() {
        super(ModuleInspectorPlugin.class);
    }
    
    public ModuleInspector createModuleInspector() {
        for (ModuleInspectorPlugin provider : getPlugins()) {
            // TODO: if more than one inspector is available, we should combine them into a single one
            ModuleInspector inspector = provider.createModuleInspector();
            if (inspector != null) {
                return inspector;
            }
        }
        return null;
    }
}
