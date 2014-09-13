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
package com.googlecode.arit.websphere.orb;

import java.util.Iterator;

import com.googlecode.arit.Formatter;
import com.googlecode.arit.resource.ResourceType;
import com.googlecode.arit.resource.SimpleResourceEnumerator;

public class WLMProxyEnumerator extends SimpleResourceEnumerator {
    private final ResourceType resourceType;
    private final Iterator<MasterProxyRBean> iterator;
    private MasterProxyRBean proxy;
    private Object servant;
    
    public WLMProxyEnumerator(ResourceType resourceType, Iterator<MasterProxyRBean> iterator) {
        this.resourceType = resourceType;
        this.iterator = iterator;
    }

    @Override
    protected boolean doNextResource() {
        while (true) {
            if (iterator.hasNext()) {
                proxy = iterator.next();
                IORRBean ior = proxy.getIOR();
                if (ior != null) {
                    servant = ior.getServant();
                    if (servant != null) {
                        return true;
                    }
                }
            } else {
                return false;
            }
        }
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public Object getResourceObject() {
        return proxy._getTargetObject();
    }

    public String getResourceDescription(Formatter formatter) {
        return "WLM proxy";
    }

    public ClassLoader getReferencedClassLoader() {
        return servant.getClass().getClassLoader();
    }

    public String getClassLoaderReferenceDescription(Formatter formatter) {
        return "Servant: " + servant.getClass().getName();
    }

    public boolean cleanup() {
        // TODO Auto-generated method stub
        return false;
    }
}
