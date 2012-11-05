/*
 * Copyright 2010-2012 Andreas Veithen
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.googlecode.arit.CleanerPlugin;
import com.googlecode.arit.Logger;
import com.googlecode.arit.ResourceEnumeratorFactory;
import com.googlecode.arit.ResourceType;
import com.googlecode.arit.rbeans.RBeanFactory;
import com.googlecode.arit.rbeans.RBeanFactoryException;

public class JAXBUtilsPoolResourceEnumeratorFactory implements ResourceEnumeratorFactory<JAXBUtilsPoolResourceEnumerator>, CleanerPlugin {
    private final Log log = LogFactory.getLog(JAXBUtilsPoolResourceEnumeratorFactory.class);
    
    private final RBeanFactory rbf;
    private final JAXBUtilsRBean jaxbUtils;

    @Autowired
    @Qualifier("ws-cached-jaxb-object")
    private ResourceType resourceType;
    
    public JAXBUtilsPoolResourceEnumeratorFactory() {
        RBeanFactory rbf;
        try {
            rbf = new RBeanFactory(JAXBUtilsRBean.class, JAXBContextImplRBean.class);
        } catch (RBeanFactoryException ex) {
            rbf = null;
        }
        this.rbf = rbf;
        jaxbUtils = rbf == null ? null : rbf.createRBean(JAXBUtilsRBean.class);
    }
    
    public boolean isAvailable() {
        return rbf != null;
    }

    public String getDescription() {
        return "Cached JAXB objects";
    }
    
    private Map<Class<?>,PoolRBean> getPools() {
        Map<Class<?>,PoolRBean> pools = new HashMap<Class<?>,PoolRBean>();
        pools.put(JAXBIntrospector.class, jaxbUtils.getIPool());
        pools.put(Marshaller.class, jaxbUtils.getMPool());
        pools.put(Unmarshaller.class, jaxbUtils.getUPool());
        return pools;
    }
    
    public JAXBUtilsPoolResourceEnumerator createEnumerator(Logger logger) {
        return new JAXBUtilsPoolResourceEnumerator(this, resourceType, getPools(), logger);
    }
    
    public Set<ClassLoader> getClassLoaders(JAXBContext context, Logger logger) {
        Set<ClassLoader> classLoaders = new HashSet<ClassLoader>();
        if (rbf.getRBeanInfo(JAXBContextImplRBean.class).getTargetClass().isInstance(context)) {
            JAXBModelRBean model = rbf.createRBean(JAXBContextImplRBean.class, context).getModel();
            // The model may be null if the JAXBContextImpl failed to create the model
            if (model != null) {
                for (ValueTypeInformationRBean typeInformation : model.getTypeInformation()) {
                    classLoaders.add(typeInformation.getJavaType().getClassLoader());
                }
            }
        } else {
            logger.log("Encountered unexpected JAXBContext implementation " + context.getClass().getName());
        }
        return classLoaders;
    }

    public void clean(ClassLoader classLoader) {
        for (Map.Entry<Class<?>,PoolRBean> poolsEntry : getPools().entrySet()) {
            Class<?> pooledObjectType = poolsEntry.getKey();
            Map<JAXBContext,List<?>> map = poolsEntry.getValue().getSoftMap().get();
            if (map != null) {
                // The map is a ConcurrentHashMap, so no synchronization is required
                for (Iterator<Map.Entry<JAXBContext,List<?>>> it = map.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry<JAXBContext,List<?>> entry = it.next();
                    // TODO: invocation of clean should also get a Logger instance
                    if (getClassLoaders(entry.getKey(), Logger.NULL).contains(classLoader)) {
                        it.remove();
                        log.info("Removed pooled " + pooledObjectType.getSimpleName() + "(s)");
                    }
                }
            }
        }
    }
}
