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
package com.googlecode.arit;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

public abstract class AbstractPluginManager<T extends Plugin> implements Initializable {
    private final Class<T> pluginClass;
    
    @Requirement
    private PlexusContainer container;
    
    public AbstractPluginManager(Class<T> pluginClass) {
        this.pluginClass = pluginClass;
    }
    
    public void initialize() throws InitializationException {
        try {
            List<T> availablePlugins = new ArrayList<T>();
            for (T plugin : container.lookupList(pluginClass)) {
                if (plugin.isAvailable()) {
                    availablePlugins.add(plugin);
                }
            }
            initialize(availablePlugins);
        } catch (ComponentLookupException ex) {
            throw new InitializationException("Failed to lookup components with role " + pluginClass, ex);
        }
    }

    protected abstract void initialize(List<T> availablePlugins);
}
