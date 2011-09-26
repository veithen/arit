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

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.googlecode.arit.ResourceEnumeratorFactory;
import com.googlecode.arit.ResourceType;
import com.googlecode.arit.rbeans.RBeanFactory;
import com.googlecode.arit.rbeans.RBeanFactoryException;

public class JAXBUtilsPoolResourceEnumeratorFactory implements ResourceEnumeratorFactory<JAXBUtilsPoolResourceEnumerator> {
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

    public JAXBUtilsPoolResourceEnumerator createEnumerator() {
        Map<Class<?>,PoolRBean> pools = new HashMap<Class<?>,PoolRBean>();
        pools.put(JAXBIntrospector.class, jaxbUtils.getIPool());
        pools.put(Marshaller.class, jaxbUtils.getMPool());
        pools.put(Unmarshaller.class, jaxbUtils.getUPool());
        return new JAXBUtilsPoolResourceEnumerator(resourceType, rbf, pools);
    }
}
