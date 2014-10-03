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
package com.googlecode.arit.jcl;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.googlecode.arit.Formatter;
import com.googlecode.arit.resource.ClassLoaderReference;
import com.googlecode.arit.resource.CleanerPlugin;
import com.googlecode.arit.resource.Resource;
import com.googlecode.arit.resource.ResourceScanner;
import com.googlecode.arit.resource.ResourceType;
import com.googlecode.arit.resource.SimpleClassLoaderReference;

public class LogFactoryResourceScanner implements ResourceScanner, CleanerPlugin {
	@Autowired
	private LogFactoryLoader logFactoryLoader;

	@Autowired
	@Qualifier("jcl-factory")
	private ResourceType resourceType;

	public String getDescription() {
		return "Cached JCL LogFactory instances";
	}

	public boolean isAvailable() {
		return logFactoryLoader.isAvailable();
	}

	public class LogFactoryResource implements Resource<Object> {

		private final ClassLoader classLoader;
		private final Object factory;
		private final String logFactoryDescription;

		public LogFactoryResource(Entry<ClassLoader, Object> factoryEntry, String logFactoryDescription) {
			this.logFactoryDescription = logFactoryDescription;
			classLoader = factoryEntry.getKey();
			factory = factoryEntry.getValue();
		}

		public ResourceType getResourceType() {
			return resourceType;
		}

		public Object getResourceObject() {
			return factory;
		}

		public String getDescription(Formatter formatter) {
			return "LogFactory instance cached by " + logFactoryDescription + "; class="
					+ factory.getClass().getName();
		}

		public boolean isGarbageCollectable() {
			// TODO check this
			return false;
		}

		public Set<ClassLoaderReference> getClassLoaderReferences() {
			Set<ClassLoaderReference> clRefs = new HashSet<ClassLoaderReference>(1);
			clRefs.add(new SimpleClassLoaderReference(classLoader, "Cache key"));
			return clRefs;
		}
    }

	public void clean(ClassLoader classLoader) {
		for (LogFactoryRef logFactoryRef : logFactoryLoader.getLogFactories()) {
			Map<ClassLoader, Object> factories = logFactoryRef.getFactory().getFactories();
			// This may indeed be null if no factories have been cached yet
			if (factories != null) {
				factories.remove(classLoader);
			}
		}
	}

	public void scanForResources(ResourceListener resourceEventListener) {
		for (LogFactoryRef logFactoryRef :logFactoryLoader.getLogFactories()) {
			Map<ClassLoader, Object> factories = logFactoryRef.getFactory().getFactories();
			// This may indeed be null if no factories have been cached yet
			if (factories != null) {
				for (Entry<ClassLoader, Object> entry : factories.entrySet()) {
					resourceEventListener.onResourceFound(new LogFactoryResource(entry, logFactoryRef.getDescription()));
				}
			}
		}
	}
}
