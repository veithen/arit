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
package com.googlecode.arit.websphere.naming;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.googlecode.arit.ResourceEnumeratorFactory;
import com.googlecode.arit.ResourceType;
import com.googlecode.arit.rbeans.RBeanFactory;
import com.googlecode.arit.rbeans.RBeanFactoryException;

public class NamingCacheEnumeratorFactory implements ResourceEnumeratorFactory<NamingCacheEnumerator> {
    private final CacheManagerRBean rbean;
    
    @Autowired
    @Qualifier("ws-naming-cache")
    private ResourceType resourceType;
    
    public NamingCacheEnumeratorFactory() {
        CacheManagerRBean rbean;
        try {
            rbean = new RBeanFactory(CacheManagerRBean.class).createRBean(CacheManagerRBean.class);
        } catch (RBeanFactoryException ex) {
            rbean = null;
        }
        this.rbean = rbean;
    }

    public boolean isAvailable() {
        return rbean != null;
    }

    public String getDescription() {
        return "Cached JNDI lookups";
    }
    
    public NamingCacheEnumerator createEnumerator() {
        return new NamingCacheEnumerator(resourceType, rbean.getCaches());
    }
}
