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
package com.googlecode.arit.websphere.jaxb;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;

import com.googlecode.arit.Formatter;
import com.googlecode.arit.ResourceEnumerator;
import com.googlecode.arit.ResourceType;
import com.googlecode.arit.rbeans.RBeanFactory;

public class JAXBUtilsPoolResourceEnumerator implements ResourceEnumerator {
    private final ResourceType resourceType;
    private final RBeanFactory rbf;
    private final Iterator<Map.Entry<Class<?>,PoolRBean>> poolIterator;
    private Class<?> cachedObjectType;
    private Iterator<Map.Entry<JAXBContext,List<?>>> contextIterator;
    private Map.Entry<JAXBContext,List<?>> entry;
    private Iterator<ClassLoader> classLoaderIterator;
    private ClassLoader classLoader;
    
    public JAXBUtilsPoolResourceEnumerator(ResourceType resourceType, RBeanFactory rbf, Map<Class<?>,PoolRBean> poolMap) {
        this.resourceType = resourceType;
        this.rbf = rbf;
        poolIterator = poolMap.entrySet().iterator();
    }
    
    public boolean nextResource() {
        while (true) {
            if (contextIterator == null) {
                if (poolIterator.hasNext()) {
                    Map.Entry<Class<?>,PoolRBean> entry = poolIterator.next();
                    cachedObjectType = entry.getKey();
                    Map<JAXBContext,List<?>> map = entry.getValue().getSoftMap().get();
                    if (map != null) {
                        contextIterator = map.entrySet().iterator();
                    }
                } else {
                    return false;
                }
            } else {
                if (contextIterator.hasNext()) {
                    entry = contextIterator.next();
                    Set<ClassLoader> classLoaders = new HashSet<ClassLoader>();
                    for (ValueTypeInformationRBean typeInformation : rbf.createRBean(JAXBContextImplRBean.class, entry.getKey()).getModel().getTypeInformation()) {
                        classLoaders.add(typeInformation.getJavaType().getClassLoader());
                    }
                    classLoaderIterator = classLoaders.iterator();
                    return true;
                } else {
                    contextIterator = null;
                }
            }
        }
    }
    
    public ResourceType getResourceType() {
        return resourceType;
    }
    
    public Object getResourceObject() {
        return entry;
    }
    
    public String getResourceDescription(Formatter formatter) {
        return "Cached " + cachedObjectType.getSimpleName();
    }
    
    public boolean nextClassLoaderReference() {
        if (classLoaderIterator.hasNext()) {
            classLoader = classLoaderIterator.next();
            return true;
        } else {
            return false;
        }
    }
    
    public String getClassLoaderReferenceDescription(Formatter formatter) {
        return "JAXBContext";
    }
    
    public ClassLoader getReferencedClassLoader() {
        return classLoader;
    }

    public boolean cleanup() {
        // TODO Auto-generated method stub
        return false;
    }
}
