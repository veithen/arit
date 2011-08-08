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
package com.googlecode.arit.axis2;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.googlecode.arit.ModuleDescription;
import com.googlecode.arit.ModuleInspector;
import com.googlecode.arit.ModuleInspectorPlugin;
import com.googlecode.arit.ModuleStatus;
import com.googlecode.arit.ModuleType;

public class Axis2ModuleInspectorPlugin implements ModuleInspectorPlugin {
    @Autowired
    @Qualifier("mar")
    ModuleType marModuleType;
    
    @Autowired
    @Qualifier("aar")
    ModuleType aarModuleType;

    public boolean isAvailable() {
        return true;
    }

    public ModuleInspector createModuleInspector() {
        return new ModuleInspector() {
            public List<ModuleDescription> listModules() {
                return null;
            }
            
            public ModuleDescription inspect(ClassLoader classLoader) {
                if (classLoader instanceof URLClassLoader) {
                    URLClassLoader cl = (URLClassLoader)classLoader;
                    String clClassName = cl.getClass().getName();
                    if (clClassName.equals("org.apache.axis2.deployment.DeploymentClassLoader") || clClassName.equals("org.apache.axis2.classloader.JarFileClassLoader")) {
                        URL url = cl.getURLs()[0];
                        return new ModuleDescription(cl.findResource("META-INF/module.xml") == null ? aarModuleType : marModuleType, url.getFile(), classLoader, url, ModuleStatus.UNKNOWN);
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            }
        };
    }
}
