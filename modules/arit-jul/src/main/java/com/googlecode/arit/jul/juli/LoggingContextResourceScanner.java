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
package com.googlecode.arit.jul.juli;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.github.veithen.rbeans.RBeanFactory;
import com.github.veithen.rbeans.RBeanFactoryException;
import com.googlecode.arit.Formatter;
import com.googlecode.arit.jul.Utils;
import com.googlecode.arit.resource.ClassLoaderReference;
import com.googlecode.arit.resource.Resource;
import com.googlecode.arit.resource.ResourceScanner;
import com.googlecode.arit.resource.ResourceType;
import com.googlecode.arit.resource.SimpleClassLoaderReference;

public class LoggingContextResourceScanner implements ResourceScanner {
	@Autowired
	@Qualifier("juli-context")
	private ResourceType resourceType;

	private final RBeanFactory rbf;
	private final ClassLoaderLogManagerRBean logManager;

	public LoggingContextResourceScanner() {
		this(LogManager.getLogManager());
	}

	LoggingContextResourceScanner(LogManager logManager) {
		RBeanFactory rbf;
		try {
			rbf = new RBeanFactory(ClassLoaderLogManagerRBean.class, ClassLoaderLogInfoRBean.class);
		} catch (RBeanFactoryException ex) {
			rbf = null;
		}
		this.rbf = rbf;
		if (rbf != null) {
			if (rbf.getRBeanInfo(ClassLoaderLogManagerRBean.class).getTargetClass().isInstance(logManager)) {
				this.logManager = rbf.createRBean(ClassLoaderLogManagerRBean.class, logManager);
			} else {
				this.logManager = null;
			}
		} else {
			this.logManager = null;
		}
	}

	public String getDescription() {
		return "JULI per class loader logging context";
	}

	public boolean isAvailable() {
		return logManager != null;
	}

	public class LoggingContextResource implements Resource<Object> {
		private ClassLoader classLoader;
		private Object object;

		public LoggingContextResource(Entry<ClassLoader, Object> contextEntry) {
			classLoader = contextEntry.getKey();
			object = contextEntry.getValue();
		}

		public Object getResourceObject() {
			return object;
		}

		public String getDescription(Formatter formatter) {
			return "JULI per class loader logging context";
		}

		public ResourceType getResourceType() {
			return resourceType;
		}

		public boolean isGarbageCollectable() {
			// TODO Check this
			return false;
		}

		public Set<ClassLoaderReference> getClassLoaderReferences() {
			Set<ClassLoaderReference> clRefs = new HashSet<ClassLoaderReference>();
			clRefs.add(new SimpleClassLoaderReference(classLoader, "Context selector"));

			ClassLoaderLogInfoRBean logInfo = rbf.createRBean(ClassLoaderLogInfoRBean.class, object);
			Collection<Logger> loggers = logInfo.getLoggers().values();
			for (Logger logger : loggers) {
				for (Handler handler : logger.getHandlers()) {
					String description = "Handler " + handler.getClass().getName() + " registered on logger "
									+ Utils.getLoggerDisplayName(logger);
					clRefs.add(new SimpleClassLoaderReference(handler.getClass().getClassLoader(), description));
				}
			}
			return clRefs;
		}
    }

	public void scanForResources(ResourceListener resourceEventListener) {
		Set<Entry<ClassLoader, Object>> contextSet = logManager.getClassLoaderLogInfoMap().entrySet();
		for (Entry<ClassLoader, Object> entry : contextSet) {
			resourceEventListener.onResourceFound(new LoggingContextResource(entry));
		}
	}
}
