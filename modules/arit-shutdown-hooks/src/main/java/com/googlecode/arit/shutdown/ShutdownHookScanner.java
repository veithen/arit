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
package com.googlecode.arit.shutdown;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.googlecode.arit.Formatter;
import com.googlecode.arit.resource.ClassLoaderReference;
import com.googlecode.arit.resource.Resource;
import com.googlecode.arit.resource.ResourceScanner;
import com.googlecode.arit.resource.ResourceType;
import com.googlecode.arit.threadutils.ThreadHelper;
import com.googlecode.arit.threadutils.ThreadUtils;

public class ShutdownHookScanner implements ResourceScanner {

	@Autowired
	@Qualifier("shutdown-hook")
	private ResourceType resourceType;

	@Autowired
	private ShutdownHookInspector inspector;

	@Autowired
	private ThreadHelper threadHelper;

	public boolean isAvailable() {
		return inspector.isAvailable() && threadHelper.isAvailable();
	}

	public String getDescription() {
		return "Shutdown hooks";
	}

    public ResourceType getResourceType() {
        return resourceType;
    }

	public class ShutdownHookResource implements Resource<Thread> {

		private final Thread threadObject;
		private final Set<ClassLoaderReference> classLoaderReferences;

		public ShutdownHookResource(Thread threadObject, Set<ClassLoaderReference> classLoaderReferences) {
			this.threadObject = threadObject;
			this.classLoaderReferences = classLoaderReferences;
		}
		
		public final ResourceType getResourceType() {
			return resourceType;
		}

		public Thread getResourceObject() {
			return threadObject;
		}

		public String getDescription(Formatter formatter) {
			return "Shutdown hook; type=" + threadObject.getClass().getName();
		}

		public Set<ClassLoaderReference> getClassLoaderReferences() {
			return classLoaderReferences;
		}

		public boolean isGarbageCollectable() {
			return false;
		}

	}

	public void scanForResources(ResourceListener resourceEventListener) {
		List<Thread> shutdownHooks = inspector.getShutdownHooks();
		for (Thread threadObject : shutdownHooks) {
			Set<ClassLoaderReference> classLoaderRefsOfThread = ThreadUtils.getClassLoaderRefsOfThread(threadObject, threadHelper);
			resourceEventListener.onResourceFound(new ShutdownHookResource(threadObject, classLoaderRefsOfThread));
		}
	}
}
