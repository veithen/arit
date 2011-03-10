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
package com.googlecode.arit.websphere;

import org.codehaus.plexus.component.annotations.Component;

import com.googlecode.arit.jcl.LogFactoryLoaderPlugin;

@Component(role=LogFactoryLoaderPlugin.class, hint="was-axis2")
public class JAXWSRuntimeLogFactoryLoaderPlugin implements LogFactoryLoaderPlugin {
    private final ClassLoader classLoader;
    
    public JAXWSRuntimeLogFactoryLoaderPlugin() {
        Class<?> axis2Class;
        try {
            axis2Class = Class.forName("org.apache.axis2.Version");
        } catch (ClassNotFoundException ex) {
            axis2Class = null;
        }
        classLoader = axis2Class == null ? null : axis2Class.getClassLoader();
    }

    public boolean isAvailable() {
        return classLoader != null;
    }

    public String getDescription() {
        return "JAX-WS (Axis2) runtime";
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }
}
