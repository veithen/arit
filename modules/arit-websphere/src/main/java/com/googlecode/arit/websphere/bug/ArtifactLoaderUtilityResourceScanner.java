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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.github.veithen.rbeans.RBeanFactory;
import com.github.veithen.rbeans.RBeanFactoryException;
import com.googlecode.arit.resource.CleanerPlugin;
import com.googlecode.arit.resource.ResourceScanner;
import com.googlecode.arit.resource.ResourceType;
import com.googlecode.arit.resource.SimpleResource;

public class ArtifactLoaderUtilityResourceScanner implements ResourceScanner, CleanerPlugin {
    private final ArtifactLoaderUtilityRBean rbean;
    
    @Autowired
    @Qualifier("websphere-bug")
    private ResourceType resourceType;
    
    public ArtifactLoaderUtilityResourceScanner() {
        ArtifactLoaderUtilityRBean rbean;
        try {
            rbean = new RBeanFactory(ArtifactLoaderUtilityRBean.class).createRBean(ArtifactLoaderUtilityRBean.class);
        } catch (RBeanFactoryException ex) {
            rbean = null;
        }
        this.rbean = rbean;
    }

    public String getDescription() {
        return "ArtifactLoaderUtility#appNameCache entries (JR40014)";
    }

    public boolean isAvailable() {
        return rbean != null;
    }

	public void scanForResources(ResourceListener resourceEventListener) {
		String description = "ArtifactLoaderUtility#appNameCache entry (JR40014)";
        for (ClassLoader cl : rbean.getAppNameCache().keySet()) {
			SimpleResource<ClassLoader> resource = new SimpleResource<ClassLoader>(resourceType, cl, description);
			resource.addClassloaderReference(cl,"Cache key");
			resourceEventListener.onResourceFound(resource);
		}
    }

	public void clean(ClassLoader classLoader) {
		rbean.getAppNameCache().remove(classLoader);
	}

}
