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
package com.googlecode.arit.rmi;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.googlecode.arit.ResourceEnumerator;
import com.googlecode.arit.ResourceType;

public class RmiExportEnumerator implements ResourceEnumerator {
    private final ResourceType resourceType;
    private final Iterator<Object> iterator;
    private Object exportedObject;
    
    public RmiExportEnumerator(ResourceType resourceType, List<Object> exportedObjects) {
        this.resourceType = resourceType;
        iterator = exportedObjects.iterator();
    }

    public ResourceType getType() {
        return resourceType;
    }

    public Collection<ClassLoader> getClassLoaders() {
        return Collections.singleton(exportedObject.getClass().getClassLoader());
    }

    public String getDescription() {
        return "Exported object (RMI): " + exportedObject.getClass().getName();
    }

    public boolean next() {
        if (iterator.hasNext()) {
            exportedObject = iterator.next();
            return true;
        } else {
            return false;
        }
    }
}
