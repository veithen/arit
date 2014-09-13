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
package com.googlecode.arit.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PriorityBasedPluginManager<T extends PriorityBasedPlugin> extends AbstractPluginManager<T> {
    private List<T> plugins;
    
    public PriorityBasedPluginManager(Class<T> pluginClass) {
        super(pluginClass);
    }

    @Override
    protected void initialize(List<T> availablePlugins) {
        plugins = new ArrayList<T>(availablePlugins);
        Collections.sort(plugins, new Comparator<T>() {
            public int compare(T o1, T o2) {
                return o2.getPriority()-o1.getPriority();
            }
        });
    }

    public boolean isAvailable() {
        return !plugins.isEmpty();
    }
    
    protected List<T> getPlugins() {
        return plugins;
    }
}
