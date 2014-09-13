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

import java.util.Enumeration;

import com.googlecode.arit.Formatter;
import com.googlecode.arit.resource.ResourceType;
import com.googlecode.arit.resource.SimpleResourceEnumerator;

public class CachedServantObjectEnumerator extends SimpleResourceEnumerator {
    private final ResourceType resourceType;
    private final Enumeration<ObjectImplRBean> e;
    private Object servant;
    
    public CachedServantObjectEnumerator(ResourceType resourceType, Enumeration<ObjectImplRBean> e) {
        this.resourceType = resourceType;
        this.e = e;
    }

    @Override
    protected boolean doNextResource() {
        while (true) {
            if (e.hasMoreElements()) {
                DelegateRBean delegate = e.nextElement().getDelegate();
                if (delegate instanceof ClientDelegateRBean) {
                    servant = ((ClientDelegateRBean)delegate).getIOR().getServant();
                    return true;
                }
            } else {
                servant = null;
                return false;
            }
        }
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public Object getResourceObject() {
        return servant;
    }

    public String getClassLoaderReferenceDescription(Formatter formatter) {
        return "Servant: " + servant.getClass().getName();
    }

    public ClassLoader getReferencedClassLoader() {
        return servant.getClass().getClassLoader();
    }

    public String getResourceDescription(Formatter formatter) {
        return "Cached servant object";
    }

    public boolean cleanup() {
        return false;
    }
}
