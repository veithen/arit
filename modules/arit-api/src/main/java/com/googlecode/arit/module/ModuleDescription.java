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
package com.googlecode.arit.module;

import java.net.URL;

public class ModuleDescription {
    private final ModuleType type;
    private final String displayName;
    private final ClassLoader classLoader;
    private final URL url;
    private final ModuleStatus status;
    
    public ModuleDescription(ModuleType type, String displayName, ClassLoader classLoader, URL url, ModuleStatus status) {
        this.type = type;
        this.displayName = displayName;
        this.classLoader = classLoader;
        this.url = url;
        this.status = status;
    }

    public ModuleType getType() {
        return type;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get the class loader for the module. If the module description was created by
     * {@link ModuleInspector#inspect(ClassLoader)}, then this must be the class loader passed to
     * that method.
     * 
     * @return the class loader for the module
     */
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public URL getUrl() {
        return url;
    }

    public ModuleStatus getStatus() {
        return status;
    }
}
