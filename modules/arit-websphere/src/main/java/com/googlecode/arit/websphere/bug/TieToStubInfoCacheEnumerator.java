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
package com.googlecode.arit.websphere.bug;

import java.util.Iterator;
import java.util.Map;

import com.googlecode.arit.Formatter;
import com.googlecode.arit.ResourceEnumerator;
import com.googlecode.arit.ResourceType;
import com.googlecode.arit.rbeans.RBeanFactory;

public class TieToStubInfoCacheEnumerator implements ResourceEnumerator {
    private final ResourceType resourceType;
    private final RBeanFactory rbf;
    private final Map<?,?>[] maps;
    private int mapIndex;
    private Iterator<?> valueIterator;
    private DataValueListEntryRBean listEntry;
    private Object tie;
    private Object stub;
    private int clRef;
    
    public TieToStubInfoCacheEnumerator(ResourceType resourceType, RBeanFactory rbf, Map<?,?>[] maps) {
        this.resourceType = resourceType;
        this.rbf = rbf;
        this.maps = maps;
    }

    public boolean nextResource() {
        while (true) {
            while (true) {
                if (listEntry != null) {
                    Object next = listEntry.getNext();
                    listEntry = next == null ? null : rbf.createRBean(DataValueListEntryRBean.class, next);
                    if (listEntry != null) {
                        break;
                    }
                } else if (valueIterator == null) {
                    if (mapIndex == maps.length) {
                        return false;
                    } else {
                        valueIterator = maps[mapIndex].values().iterator();
                    }
                } else {
                    if (valueIterator.hasNext()) {
                        listEntry = rbf.createRBean(DataValueListEntryRBean.class, valueIterator.next());
                        break;
                    } else {
                        valueIterator = null;
                        listEntry = null;
                        mapIndex++;
                    }
                }
            }
            tie = listEntry.getKey();
            stub = rbf.createRBean(StubInfoRBean.class, listEntry.getData()).getStub();
            // Apparently, in some rare cases, the result of getStub is null
            if (stub != null) {
                clRef = -1;
                return true;
            }
        }
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public Object getResourceObject() {
        return tie;
    }

    public String getResourceDescription(Formatter formatter) {
        return "Tie to stub info cache entry";
    }

    public boolean nextClassLoaderReference() {
        if (clRef < 1) {
            clRef++;
            return true;
        } else {
            return false;
        }
    }

    public String getClassLoaderReferenceDescription(Formatter formatter) {
        switch (clRef) {
            case 0: return "Tie class: " + tie.getClass().getName();
            case 1: return "Stub class: " + stub.getClass().getName();
            default:
                throw new IllegalStateException();
        }
    }

    public ClassLoader getReferencedClassLoader() {
        switch (clRef) {
            case 0: return tie.getClass().getClassLoader();
            case 1: return stub.getClass().getClassLoader();
            default:
                throw new IllegalStateException();
        }
    }
    
    public boolean cleanup() {
        return false;
    }
}
