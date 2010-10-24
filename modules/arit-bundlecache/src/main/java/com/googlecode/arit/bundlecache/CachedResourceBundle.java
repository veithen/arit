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
package com.googlecode.arit.bundlecache;

import java.util.ResourceBundle;

public class CachedResourceBundle {
    private final ClassLoader classLoader;
    private final String name;
    private final ResourceBundle bundle;
    
    public CachedResourceBundle(ClassLoader classLoader, String name, ResourceBundle bundle) {
        this.classLoader = classLoader;
        this.name = name;
        this.bundle = bundle;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public String getName() {
        return name;
    }

    public ResourceBundle getBundle() {
        return bundle;
    }
}
