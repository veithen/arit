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
package com.googlecode.arit.jvm;

import java.util.Iterator;
import java.util.List;

import com.googlecode.arit.ResourceType;
import com.googlecode.arit.SimpleResourceEnumerator;

public class JVMSingletonEnumerator extends SimpleResourceEnumerator {
    private final ResourceType resourceType;
    private final Iterator<JVMSingleton> iterator;
    private JVMSingleton singleton;
    private Object instance;
    
    public JVMSingletonEnumerator(ResourceType resourceType, List<JVMSingleton> singletons) {
        this.resourceType = resourceType;
        iterator = singletons.iterator();
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public ClassLoader getReferencedClassLoader() {
        return instance.getClass().getClassLoader();
    }

    public String getClassLoaderReferenceDescription() {
        return "Implementation class";
    }

    public Object getResourceObject() {
        return instance;
    }

    public String getResourceDescription() {
        return singleton.getDescription() + ": " + instance.getClass().getName();
    }

    protected boolean doNextResource() {
        while (iterator.hasNext()) {
            singleton = iterator.next();
            instance = singleton.getInstance();
            if (instance != null) {
                return true;
            }
        }
        return false;
    }

    public boolean cleanup() {
        return false;
    }
}
