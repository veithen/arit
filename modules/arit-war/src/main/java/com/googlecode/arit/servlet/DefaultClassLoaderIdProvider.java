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
package com.googlecode.arit.servlet;

import java.util.Map;
import java.util.WeakHashMap;

import org.codehaus.plexus.component.annotations.Component;

@Component(role=ClassLoaderIdProvider.class)
public class DefaultClassLoaderIdProvider implements ClassLoaderIdProvider {
    private final Map<ClassLoader,Integer> idMap = new WeakHashMap<ClassLoader,Integer>();
    private int nextId = 1;

    public synchronized Integer getClassLoaderId(ClassLoader classLoader) {
        Integer id = idMap.get(classLoader);
        if (id == null) {
            id = nextId++;
            idMap.put(classLoader, id);
        }
        return id;
    }
}
