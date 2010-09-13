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
package com.googlecode.arit.threads;

import org.codehaus.plexus.component.annotations.Component;

import com.googlecode.arit.PriorityBasedPluginManager;

@Component(role=ThreadInspectorManager.class)
public class ThreadInspectorManager extends PriorityBasedPluginManager<ThreadInspectorPlugin> {
    public ThreadInspectorManager() {
        super(ThreadInspectorPlugin.class);
    }
    
    public ThreadDescription getDescription(Thread thread) {
        for (ThreadInspectorPlugin inspector : getPlugins()) {
            ThreadDescription description = inspector.getDescription(thread);
            if (description != null) {
                return description;
            }
        }
        return null;
    }
}
