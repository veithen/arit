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
package com.googlecode.arit.rmi;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.googlecode.arit.ResourceEnumerator;
import com.googlecode.arit.ResourceEnumeratorFactory;
import com.googlecode.arit.ResourceType;
import com.googlecode.arit.SimpleResourceEnumerator;
import com.googlecode.arit.SingletonPluginManager;

public class RmiExportEnumeratorFactory extends SingletonPluginManager<RmiExportEnumeratorPlugin> implements ResourceEnumeratorFactory<ResourceEnumerator> {
    private class RmiExportEnumeratorImpl extends SimpleResourceEnumerator {
        private final Iterator<Object> iterator;
        private Object exportedObject;
        
        RmiExportEnumeratorImpl(List<Object> exportedObjects) {
            iterator = exportedObjects.iterator();
        }

        public ResourceType getResourceType() {
            return resourceType;
        }

        public Object getResourceObject() {
            return exportedObject;
        }

        public ClassLoader getReferencedClassLoader() {
            return exportedObject.getClass().getClassLoader();
        }

        public String getClassLoaderReferenceDescription() {
            return "Exported oject implementation class";
        }

        public String getResourceDescription() {
            return "Exported object (RMI): " + exportedObject.getClass().getName();
        }

        protected boolean doNextResource() {
            if (iterator.hasNext()) {
                exportedObject = iterator.next();
                return true;
            } else {
                return false;
            }
        }

        public boolean cleanup() {
            return false;
        }
    }
    
    @Autowired
    @Qualifier("rmi-export")
    ResourceType resourceType;

    public RmiExportEnumeratorFactory() {
        super(RmiExportEnumeratorPlugin.class);
    }
    
    public String getDescription() {
        return "RMI exports";
    }

    public ResourceEnumerator createEnumerator() {
        return new RmiExportEnumeratorImpl(getPlugin().getExportedObjects());
    }
}
