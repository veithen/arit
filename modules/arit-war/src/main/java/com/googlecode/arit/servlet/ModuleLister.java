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
package com.googlecode.arit.servlet;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.component.annotations.Component;

import com.googlecode.arit.ModuleDescription;
import com.googlecode.arit.ModuleListerPlugin;
import com.googlecode.arit.PluginManager;

@Component(role=ModuleLister.class)
public class ModuleLister extends PluginManager<ModuleListerPlugin> {
    public ModuleLister() {
        super(ModuleListerPlugin.class);
    }

    public List<ModuleDescription> listModules() {
        List<ModuleDescription> result = new ArrayList<ModuleDescription>();
        for (ModuleListerPlugin plugin : getPlugins()) {
            List<ModuleDescription> descriptions = plugin.listModules();
            if (descriptions != null) {
                result.addAll(descriptions);
            }
        }
        return result;
    }
}
