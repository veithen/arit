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
package com.googlecode.arit.bundlecache;

import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.googlecode.arit.Formatter;
import com.googlecode.arit.resource.ClassLoaderReference;
import com.googlecode.arit.resource.Resource;
import com.googlecode.arit.resource.ResourceScanner;
import com.googlecode.arit.resource.ResourceScanningConfig;
import com.googlecode.arit.resource.ResourceType;
import com.googlecode.arit.resource.SimpleClassLoaderReference;

public class CachedResourceBundleScanner implements ResourceScanner {
	@Autowired
	@Qualifier("cached-resource-bundle")
	private ResourceType resourceType;

	@Autowired
	private ResourceScanningConfig config;

	@Autowired
	private ResourceBundleCacheInspector inspector;

	public boolean isAvailable() {
		return inspector.isAvailable();
	}

	public String getDescription() {
		return "Cached resource bundles";
	}
    
    public ResourceType getResourceType() {
        return resourceType;
    }

	public class CachedResourceBundleResource implements Resource<ResourceBundle> {

		private final ClassLoader classLoader;
		private final String name;
		private final ResourceBundle bundle;

		public CachedResourceBundleResource(CachedResourceBundle cachedResourceBundle) {
			this.classLoader = cachedResourceBundle.getClassLoader();
			this.name = cachedResourceBundle.getName();
			this.bundle = cachedResourceBundle.getBundle();
		}

		public ResourceType getResourceType() {
			return resourceType;
		}

		public ResourceBundle getResourceObject() {
			return bundle;
		}

		public String getDescription(Formatter formatter) {
			return "Cached resource bundle: " + name;
		}

		public boolean isGarbageCollectable() {
			return true;
		}

		public Set<ClassLoaderReference> getClassLoaderReferences() {
			Set<ClassLoaderReference> clRefs = new HashSet<ClassLoaderReference>(1);
			clRefs.add(new SimpleClassLoaderReference(classLoader, "Bundle class loader"));
			return clRefs;
		}

	}

	public void scanForResources(ResourceListener resourceEventListener) {
		if (!config.includeGarbageCollectableResources()) {
			return;
		}
		List<CachedResourceBundle> cachedResourceBundles = inspector.getCachedResourceBundles();
		for (CachedResourceBundle cachedResourceBundle : cachedResourceBundles) {
			resourceEventListener.onResourceFound(new CachedResourceBundleResource(cachedResourceBundle));
		}
	}

}
