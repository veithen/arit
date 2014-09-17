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
package com.googlecode.arit.websphere;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.github.veithen.rbeans.RBeanFactory;
import com.github.veithen.rbeans.RBeanFactoryException;
import com.googlecode.arit.Messages;
import com.googlecode.arit.resource.ResourceEnumeratorFactory;
import com.googlecode.arit.resource.ResourceType;
import com.googlecode.arit.websphere.bug.PerClassLoaderCacheResourceEnumerator;

// TODO: this may apply to other JREs than WebSphere
// Note: primarily useful to identify applications with leaks; the cache uses a WeakHashMap and will never cause a leak
public class InitialContextPropsCacheEnumeratorFactory implements ResourceEnumeratorFactory<PerClassLoaderCacheResourceEnumerator> {
    private final InitialContextRBean rbean;
    
    @Autowired
    @Qualifier("initial-context-props-cache")
    private ResourceType resourceType;
    
    public InitialContextPropsCacheEnumeratorFactory() {
        InitialContextRBean rbean;
        try {
            rbean = new RBeanFactory(InitialContextRBean.class).createRBean(InitialContextRBean.class);
        } catch (RBeanFactoryException ex) {
            rbean = null;
        }
        this.rbean = rbean;
    }

    public String getDescription() {
        return "Cached initial context properties";
    }

    public boolean isAvailable() {
        return rbean != null;
    }

    public PerClassLoaderCacheResourceEnumerator createEnumerator(Messages logger) {
        return new PerClassLoaderCacheResourceEnumerator(resourceType, "Cached initial context properties", rbean.getPropsCache());
    }
}
