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
package com.googlecode.arit.jmx;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.googlecode.arit.module.ModuleDescription;
import com.googlecode.arit.plugin.PluginManager;
import com.googlecode.arit.report.ClassLoaderIdProvider;
import com.googlecode.arit.resource.CleanerPlugin;
import com.googlecode.arit.servlet.ModuleInspectorFactory;

@ManagedResource(objectName="com.googlecode.arit:type=Cleaner", description="Cleans resources that cause memory leaks")
public class Cleaner extends PluginManager<CleanerPlugin> {
    private static final Log log = LogFactory.getLog(Cleaner.class);
    
    @Autowired
    private ClassLoaderIdProvider classLoaderIdProvider;
    
    @Autowired
    private ModuleInspectorFactory moduleInspectorFactory;
    
    public Cleaner() {
        super(CleanerPlugin.class);
    }

    @ManagedOperation(description="Clean resources linked to a given class loader")
    @ManagedOperationParameters(
        @ManagedOperationParameter(name="classLoaderId", description="The ID of the class loader")
    )
    public void clean(Integer classLoaderId) {
        ClassLoader classLoader = classLoaderIdProvider.getClassLoader(classLoaderId);
        if (classLoader == null) {
            log.info("Class loader with ID " + classLoaderId + " not found");
        } else {
            ModuleDescription moduleDescription = moduleInspectorFactory.createModuleInspector().inspect(classLoader);
            log.info("Starting cleanup for " + moduleDescription.getDisplayName() + " (" + classLoaderId + ")");
            for (CleanerPlugin plugin : getPlugins()) {
                try {
                    plugin.clean(classLoader);
                } catch (RuntimeException ex) {
                    log.error("Caught runtime exception", ex);
                    throw ex;
                }
            }
        }
    }
}
