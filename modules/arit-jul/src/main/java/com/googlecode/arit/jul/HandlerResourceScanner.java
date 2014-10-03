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
package com.googlecode.arit.jul;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.googlecode.arit.Formatter;
import com.googlecode.arit.resource.ClassLoaderReference;
import com.googlecode.arit.resource.CleanerPlugin;
import com.googlecode.arit.resource.Resource;
import com.googlecode.arit.resource.ResourceScanner;
import com.googlecode.arit.resource.ResourceType;
import com.googlecode.arit.resource.SimpleClassLoaderReference;

public class HandlerResourceScanner implements ResourceScanner, CleanerPlugin {
    
	@Autowired
	@Qualifier("jul-handler")
	private ResourceType resourceType;

	private static final Log LOG = LogFactory.getLog(HandlerResourceScanner.class);

	private final LogManager logManager;

	public HandlerResourceScanner() {
		LogManager logManager = LogManager.getLogManager();
		Class<? extends LogManager> clazz = logManager.getClass();
		try {
			// We only enable the plugin if the getLoggerNames method has not been overridden.
			// An overridden getLoggerNames method is an indication that the log manager maintains
			// multiple logger name spaces and that we need a specialized plugin. This is the
			// case for Tomcat's ClassLoaderLogManager. Note that WebSphere's WsLogManager
			// overrides getLogger, but not getLoggerNames.
			if (clazz.getMethod("getLoggerNames").getDeclaringClass().equals(LogManager.class)) {
				this.logManager = logManager;
			} else {
				this.logManager = null;
			}
		} catch (NoSuchMethodException ex) {
			throw new NoSuchMethodError(ex.getMessage());
		}
    }
    
	public String getDescription() {
		return "java.util.logging (JUL) handlers";
    }

	public boolean isAvailable() {
		return logManager != null;
    }

	public class HandleResource implements Resource<Handler> {
		private final Handler handler;
		private final Logger logger;

		public HandleResource(Handler handler, Logger logger) {
			this.handler = handler;
			this.logger = logger;
		}

		public ResourceType getResourceType() {
			return resourceType;
		}

		public Handler getResourceObject() {
			return handler;
		}

		public String getDescription(Formatter formatter) {
			return "JUL handler " + handler.getClass().getName() + " registered on logger "
					+ Utils.getLoggerDisplayName(logger);
		}

		public boolean isGarbageCollectable() {
			// TODO check this; should be true probably.
			return false;
		}

		public Set<ClassLoaderReference> getClassLoaderReferences() {
			Set<ClassLoaderReference> classLoaderReferences = new HashSet<ClassLoaderReference>(1);
			classLoaderReferences.add(new SimpleClassLoaderReference(handler.getClass().getClassLoader(),
					"Implementation class"));
			return classLoaderReferences;
		}
    }

	public void clean(ClassLoader classLoader) {
		Enumeration<String> loggerNames = logManager.getLoggerNames();
		while (loggerNames.hasMoreElements()) {
			Logger logger = logManager.getLogger(loggerNames.nextElement());
			// On some JREs, Logger instances may be garbage collected. In this case,
			// the enumeration returned by getLoggerNames may contain names of garbage
			// collected loggers, and getLogger will return null for these names.
			// This was observed with Sun JRE 1.6. Loggers are not garbage collectable
			// with Sun JRE 1.5, IBM JRE 1.5 and IBM JRE 1.6 (WAS 7.0).
			if (logger != null) {
				Handler[] handlers = logger.getHandlers();
				for (Handler handler : handlers) {
					if (classLoader.equals(handler.getClass().getClassLoader())) {
						logger.removeHandler(handler);
						LOG.info("Removed JUL handler " + handler.getClass().getName());
					}
				}
			}
		}
    }

	public void scanForResources(ResourceListener resourceEventListener) {
		Enumeration<String> loggerNames = logManager.getLoggerNames();
		while (loggerNames.hasMoreElements()) {
			Logger logger = logManager.getLogger(loggerNames.nextElement());
			// On some JREs, Logger instances may be garbage collected. In this case,
			// the enumeration returned by getLoggerNames may contain names of garbage
			// collected loggers, and getLogger will return null for these names.
			// This was observed with Sun JRE 1.6. Loggers are not garbage collectable
			// with Sun JRE 1.5, IBM JRE 1.5 and IBM JRE 1.6 (WAS 7.0).
			if (logger != null) {
				Handler[] handlers = logger.getHandlers();
				for (Handler handler : handlers) {
					resourceEventListener.onResourceFound(new HandleResource(handler, logger));
				}
			}
		}
	}

}
