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
package com.googlecode.arit.jcl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.googlecode.arit.ResourceType;
import com.googlecode.arit.SimpleResourceEnumerator;

public class LogFactoryResourceEnumerator extends SimpleResourceEnumerator {
    private final ResourceType resourceType;
    private final Iterator<LogFactoryRef> iterator1;
    private Iterator<Map.Entry<ClassLoader,Object>> iterator2;
    private LogFactoryRef logFactoryRef;
    private ClassLoader classLoader;
    private Object factory;

    public LogFactoryResourceEnumerator(ResourceType resourceType, List<LogFactoryRef> logFactoryRefs) {
        this.resourceType = resourceType;
        iterator1 = logFactoryRefs.iterator();
    }

    public ResourceType getType() {
        return resourceType;
    }

    public ClassLoader getReferencedClassLoader() {
        return classLoader;
    }

    public String getClassLoaderReferenceDescription() {
        return "Cache key";
    }

    public String getResourceDescription() {
        return "LogFactory instance cached by " + logFactoryRef.getDescription() + "; class=" + factory.getClass().getName();
    }

    protected boolean doNextResource() {
        while (true) {
            if (iterator2 == null) {
                if (iterator1.hasNext()) {
                    logFactoryRef = iterator1.next();
                    iterator2 = logFactoryRef.getFactory().getFactories().entrySet().iterator();
                } else {
                    return false;
                }
            }
            if (iterator2.hasNext()) {
                Map.Entry<ClassLoader,Object> entry = iterator2.next();
                classLoader = entry.getKey();
                factory = entry.getValue();
                return true;
            } else {
                iterator2 = null;
            }
        }
    }
}
