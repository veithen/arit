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

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.arit.Formatter;
import com.googlecode.arit.resource.ResourceEnumerator;
import com.googlecode.arit.resource.ResourceType;
import com.googlecode.arit.websphere.orb.ClientDelegateRBean;
import com.googlecode.arit.websphere.orb.DelegateRBean;
import com.googlecode.arit.websphere.orb.ObjectImplRBean;

public class NamingCacheEnumerator implements ResourceEnumerator {
    private static final Log log = LogFactory.getLog(NamingCacheEnumerator.class);
    
    private final ResourceType resourceType;
    private final Iterator<CacheRBean> cacheIterator;
    private Iterator<BindingsTableDataRBean> bindingsTableIterator;
    private ClassLoader classLoader;
    private BindingsTableDataRBean data;
    private Object servant;
    private int state = -1;
    
    public NamingCacheEnumerator(ResourceType resourceType, Map<CacheKeyRBean,CacheRBean> caches) {
        if (log.isDebugEnabled()) {
            log.debug("Created " + NamingCacheEnumerator.class.getSimpleName() + "; number of caches: " + caches.size());
        }
        this.resourceType = resourceType;
        cacheIterator = caches.values().iterator();
    }

    public boolean nextResource() {
        while (true) {
            if (bindingsTableIterator == null) {
                if (cacheIterator.hasNext()) {
                    CacheRBean cache = cacheIterator.next();
                    classLoader = cache.getClassLoader();
                    Map<BindingsTableKeyRBean,BindingsTableDataRBean> bindingsTable = cache.getCache().getBindingsTable();
                    if (log.isDebugEnabled()) {
                        log.debug("Starting to scan cache; name: " + cache.getCacheName() + "; classLoader: " + cache.getClassLoader().getClass().getName() + "; bindingsTable size: " + bindingsTable.size());
                    }
                    bindingsTableIterator = bindingsTable.values().iterator();
                } else {
                    return false;
                }
            } else if (bindingsTableIterator.hasNext()) {
                data = bindingsTableIterator.next();
                Object object = data.getObject();
                servant = null;
                if (object instanceof ObjectImplRBean) {
                    DelegateRBean delegate = ((ObjectImplRBean)object).getDelegate();
                    if (delegate instanceof ClientDelegateRBean) {
                        servant = ((ClientDelegateRBean)delegate).getIOR().getServant();
                    }
                }
                if (log.isDebugEnabled()) {
                    log.debug("Got next entry; name: " + data.getName() + "; type: " + object.getClass().getName() + "; servant: " + (servant == null ? null : servant.getClass().getName()));
                }
                state = -1;
                return true;
            } else {
                bindingsTableIterator = null;
                classLoader = null;
            }
        }
    }

    public Object getResourceObject() {
        return data._getTargetObject();
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public String getResourceDescription(Formatter formatter) {
        return "Cached JNDI lookup for " + data.getName();
    }

    public boolean nextClassLoaderReference() {
        switch (state) {
            case -1:
                state = 0;
                return true;
            case 0:
                state = 1;
                return servant != null;
            case 1:
                return false;
            default:
                throw new IllegalStateException();
        }
    }

    public String getClassLoaderReferenceDescription(Formatter formatter) {
        switch (state) {
            case 0: return "Context class loader";
            case 1: return "Servant: " + servant.getClass().getName();
            default:
                throw new IllegalStateException();
        }
    }

    public ClassLoader getReferencedClassLoader() {
        switch (state) {
            case 0: return classLoader;
            case 1: return servant.getClass().getClassLoader();
            default:
                throw new IllegalStateException();
        }
    }

    public boolean cleanup() {
        // TODO Auto-generated method stub
        return false;
    }
}
