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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.googlecode.arit.resource.CleanerPlugin;
import com.googlecode.arit.resource.ResourceScanner;
import com.googlecode.arit.threadutils.ThreadHelper;
import com.googlecode.arit.threadutils.ThreadUtils;

public class ThreadScanner implements ResourceScanner, CleanerPlugin {
	private static final Log log = LogFactory.getLog(ThreadScanner.class);

	@Autowired
	private ThreadHelper threadHelper;

	@Autowired
	private ThreadInspector threadInspector;

	public void scanForResources(ResourceListener resourceEventListener) {
		Thread[] threads = ThreadUtils.getAllThreads();
		for (Thread thread : threads) {
			ThreadResource threadResource = threadInspector.getThreadResource(thread);
			threadResource.addReferencedClassLoaders(ThreadUtils.getClassLoaderRefsOfThread(thread, threadHelper));
			resourceEventListener.onResourceFound(threadResource);
		}
	}

	public boolean isAvailable() {
		return true;
	}

	public String getDescription() {
		return "Threads and timers";
	}

	public void clean(ClassLoader classLoader) {
		for (Thread thread : ThreadUtils.getAllThreads()) {
			if (thread.getContextClassLoader() == classLoader) {
				thread.setContextClassLoader(String.class.getClassLoader());
				log.info("Unset context class loader for thread " + thread.getName());
			}
		}
	}
}
