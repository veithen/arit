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
package com.googlecode.arit.report;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.component.annotations.Component;

import com.googlecode.arit.ModuleIdentityProviderPlugin;
import com.googlecode.arit.PluginManager;

@Component(role=ModuleIdentityProvider.class)
public class ModuleIdentityProvider extends PluginManager<ModuleIdentityProviderPlugin> {
    public ModuleIdentityProvider() {
        super(ModuleIdentityProviderPlugin.class);
    }
    
    public List<String> getModuleIdentities(URL url, ClassLoader classLoader) {
        List<String> identities = new ArrayList<String>();
        for (ModuleIdentityProviderPlugin plugin : getPlugins()) {
            String identity = plugin.getModuleIdentity(url, classLoader);
            if (identity != null) {
                identities.add(identity);
            }
        }
        return identities;
    }
}
