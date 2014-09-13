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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.googlecode.arit.module.ModuleDescription;
import com.googlecode.arit.module.ModuleInspector;
import com.googlecode.arit.module.ModuleInspectorPlugin;
import com.googlecode.arit.module.ModuleStatus;
import com.googlecode.arit.module.ModuleType;

public class Axis2ModuleInspectorPlugin implements ModuleInspectorPlugin {
    static final Pattern tempArchivePattern = Pattern.compile("axis2[0-9]+(.*)");
    
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
                        // Note: While a MAR always contains a module.xml file, Axis2 allows to deploy services without
                        //       a services.xml file. However, Axis2 creates a multi-level class loader hierarchy and
                        //       all these class loaders are of type DeploymentClassLoader or JarFileClassLoader. If we
                        //       don't find a module.xml or services.xml file, it is impossible to tell if the class
                        //       loader belongs to a service or if it is an intermediary class loader. Therefore we only
                        //       report class loaders as Axis2 services if we find a services.xml file.
                        ModuleType moduleType;
                        if (cl.findResource("META-INF/module.xml") != null) {
                            moduleType = marModuleType;
                        } else if (cl.findResource("META-INF/services.xml") != null) {
                            moduleType = aarModuleType;
                        } else {
                            return null;
                        }
                        // Axis2 always adds the URL of the MAR or AAR as the first entry in the classpath. However,
                        // it will also copy the archive to a temporary directory and rename it. A typical file name
                        // looks as follows:
                        //   axis26493945763371868088scripting-1.6.0.mar
                        // We do an attempt here to reconstruct the original file name.
                        URL url = cl.getURLs()[0];
                        String archiveName = url.getFile();
                        archiveName = archiveName.substring(archiveName.lastIndexOf('/')+1);
                        Matcher matcher = tempArchivePattern.matcher(archiveName);
                        if (matcher.matches()) {
                            archiveName = matcher.group(1);
                        }
                        return new ModuleDescription(moduleType, archiveName, classLoader, url, ModuleStatus.UNKNOWN);
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
