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

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

import com.googlecode.arit.ResourceEnumerator;
import com.googlecode.arit.ResourceEnumeratorFactory;
import com.googlecode.arit.ResourceType;
import com.googlecode.arit.rbeans.RBeanFactory;
import com.googlecode.arit.rbeans.RBeanFactoryException;

// TODO: there is probably some code that could be reused between this class and ArtifactLoaderUtilityResourceEnumeratorFactory
@Component(role=ResourceEnumeratorFactory.class, hint="LogFactory-bug")
public class LogFactoryResourceEnumeratorFactory implements ResourceEnumeratorFactory {
    private final LogFactoryRBean rbean;
    
    @Requirement(hint="websphere-bug")
    private ResourceType resourceType;
    
    public LogFactoryResourceEnumeratorFactory() {
        LogFactoryRBean rbean;
        try {
            rbean = new RBeanFactory(LogFactoryRBean.class).createRBean(LogFactoryRBean.class);
        } catch (RBeanFactoryException ex) {
            rbean = null;
        }
        this.rbean = rbean;
    }

    public String getDescription() {
        return "Cached LogFactory instances";
    }

    public boolean isAvailable() {
        return rbean != null;
    }

    public ResourceEnumerator createEnumerator() {
        return new PerClassLoaderCacheResourceEnumerator(resourceType, "Cached LogFactory", rbean.getFactories());
    }
}
