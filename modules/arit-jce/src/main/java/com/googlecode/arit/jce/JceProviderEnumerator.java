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
package com.googlecode.arit.jce;

import java.security.Provider;

import com.googlecode.arit.ResourceType;
import com.googlecode.arit.SimpleResourceEnumerator;

public class JceProviderEnumerator extends SimpleResourceEnumerator {
    private final Provider[] providers;
    private final ResourceType resourceType;
    private int index;

    public JceProviderEnumerator(Provider[] providers, ResourceType resourceType) {
        this.providers = providers;
        this.resourceType = resourceType;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public ClassLoader getReferencedClassLoader() {
        return providers[index].getClass().getClassLoader();
    }

    public String getClassLoaderReferenceDescription() {
        return "Implementation class";
    }

    public Object getResourceObject() {
        return providers[index];
    }

    public String getResourceDescription() {
        return "JCE provider: " + providers[index].getClass().getName();
    }

    protected boolean doNextResource() {
        index++;
        return index < providers.length;
    }

    public boolean cleanup() {
        return false;
    }
}
