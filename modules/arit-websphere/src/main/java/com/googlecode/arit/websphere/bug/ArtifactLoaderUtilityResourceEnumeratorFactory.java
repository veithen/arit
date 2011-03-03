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

@Component(role=ResourceEnumeratorFactory.class, hint="ArtifactLoaderUtility-bug")
public class ArtifactLoaderUtilityResourceEnumeratorFactory implements ResourceEnumeratorFactory {
    private final ArtifactLoaderUtilityRBean rbean;
    
    @Requirement(hint="websphere-bug")
    private ResourceType resourceType;
    
    public ArtifactLoaderUtilityResourceEnumeratorFactory() {
        ArtifactLoaderUtilityRBean rbean;
        try {
            rbean = new RBeanFactory(ArtifactLoaderUtilityRBean.class).createRBean(ArtifactLoaderUtilityRBean.class);
        } catch (RBeanFactoryException ex) {
            rbean = null;
        }
        this.rbean = rbean;
    }

    public String getDescription() {
        return "ArtifactLoaderUtility#appNameCache entries";
    }

    public boolean isAvailable() {
        return rbean != null;
    }

    public ResourceEnumerator createEnumerator() {
        return new ArtifactLoaderUtilityResourceEnumerator(resourceType, rbean.getAppNameCache().keySet().iterator());
    }
}
