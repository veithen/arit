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
package com.googlecode.arit.threads;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.googlecode.arit.Formatter;
import com.googlecode.arit.resource.ClassLoaderReference;
import com.googlecode.arit.resource.Resource;
import com.googlecode.arit.resource.ResourceScanner;
import com.googlecode.arit.resource.ResourceType;
import com.googlecode.arit.threadutils.ThreadUtils;

/**
 * Resource scanner for {@link ThreadGroup} objects. Thread groups will cause class loader leaks if
 * <ol>
 * <li>the application fails to call {@link ThreadGroup#destroy()} and
 * <li>instead of using a plain {@link ThreadGroup} object, the application extends {@link ThreadGroup}.
 * </ol>
 * 
 * @author Andreas Veithen
 */
public class ThreadGroupScanner implements ResourceScanner {
	@Autowired
	@Qualifier("threadgroup")
	private ResourceType resourceType;

	public boolean isAvailable() {
		return true;
	}

	public String getDescription() {
		return "Thread groups";
    }

	public void scanForResources(ResourceListener resourceEventListener) {
		ThreadGroup[] threadGroups = ThreadUtils.getAllThreadGroups();
		for (ThreadGroup threadGroup : threadGroups) {
			resourceEventListener.onResourceFound(new ThreadGroupResource(threadGroup));
		}

	}

	public class ThreadGroupResource implements Resource<ThreadGroup> {

		private final ThreadGroup threadGroup;

		public ThreadGroupResource(ThreadGroup threadGroup) {
			this.threadGroup = threadGroup;
		}

		public ResourceType getResourceType() {
			return resourceType;
		}

		public ThreadGroup getResourceObject() {
			return this.threadGroup;
		}

		public String getDescription(Formatter formatter) {
			return "Thread group: " + threadGroup.getName() + "; class=" + threadGroup.getClass().getName();
		}

		public Set<ClassLoaderReference> getClassLoaderReferences() {
			Set<ClassLoaderReference> classLoaderReferences = new HashSet<ClassLoaderReference>();
			classLoaderReferences.add(new ClassLoaderReference() {
				
				public String getDescription(Formatter formatter) {
					return "ThreadGroup implementation class";
				}
				
				public ClassLoader getClassLoader() {
			        return threadGroup.getClass().getClassLoader();
				}
			});
			return classLoaderReferences;
		}

		public boolean isGarbageCollectable() {
			return false;
		}

	}
}
