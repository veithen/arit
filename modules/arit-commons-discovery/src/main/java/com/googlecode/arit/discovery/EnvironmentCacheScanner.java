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
package com.googlecode.arit.discovery;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.github.veithen.rbeans.RBeanFactory;
import com.github.veithen.rbeans.RBeanFactoryException;
import com.googlecode.arit.Formatter;
import com.googlecode.arit.resource.ClassLoaderReference;
import com.googlecode.arit.resource.CleanerPlugin;
import com.googlecode.arit.resource.Resource;
import com.googlecode.arit.resource.ResourceScanner;
import com.googlecode.arit.resource.ResourceType;
import com.googlecode.arit.resource.SimpleClassLoaderReference;

public class EnvironmentCacheScanner implements ResourceScanner, CleanerPlugin {
	private static final Log LOG = LogFactory.getLog(EnvironmentCacheScanner.class);

	private final EnvironmentCacheRBean rbean;

	@Autowired
	@Qualifier("env-cache-entry")
	private ResourceType resourceType;

	public EnvironmentCacheScanner() {
		EnvironmentCacheRBean rbean;
		try {
			rbean = new RBeanFactory(EnvironmentCacheRBean.class).createRBean(EnvironmentCacheRBean.class);
		} catch (RBeanFactoryException ex) {
			rbean = null;
		}
		this.rbean = rbean;
	}

	public String getDescription() {
		return "EnvironmentCache entries";
	}

	public boolean isAvailable() {
		return rbean != null;
	}

	public void scanForResources(ResourceListener resourceEventListener) {
		Map<ClassLoader, Map<String, Object>> rootCache = rbean.getRootCache();
		for (Entry<ClassLoader, Map<String, Object>> entry : rootCache.entrySet()) {
			ClassLoader classLoader = entry.getKey();
			for (Entry<String, Object> spEntry : entry.getValue().entrySet()) {
				resourceEventListener.onResourceFound(new EnvironmentCacheResource(spEntry, classLoader));
			}
		}
	}

	public void clean(ClassLoader classLoader) {
		boolean removed;
		synchronized (rbean._getTargetClass()) {
			removed = rbean.getRootCache().remove(classLoader) != null;
		}
		if (removed) {
			LOG.info("Cleaned up EnvironmentCache entries");
		}
	}

	public class EnvironmentCacheResource implements Resource<Object> {

		private final ClassLoader classLoader;
		private final String spName;
		private final Object service;

		public EnvironmentCacheResource(Entry<String, Object> spEntry, ClassLoader cl) {
			spName = spEntry.getKey();
			service = spEntry.getValue();
			classLoader = cl;
		}

		public ResourceType getResourceType() {
			return resourceType;
		}

		public Object getResourceObject() {
			return service;
		}

		public String getDescription(Formatter formatter) {
			return "Cached service provider (commons-discovery): " + spName;
		}

		public Set<ClassLoaderReference> getClassLoaderReferences() {
			Set<ClassLoaderReference> clRefs = new HashSet<ClassLoaderReference>(2);
			clRefs.add(new SimpleClassLoaderReference(classLoader, "Class loader key"));
			clRefs.add(new SimpleClassLoaderReference(service.getClass().getClassLoader(), "Service implementation: "
					+ service.getClass().getName()));
			return clRefs;
		}

		public boolean isGarbageCollectable() {
			// TODO check this
			return false;
		}
	}
}
