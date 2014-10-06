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

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.github.veithen.rbeans.RBeanFactory;
import com.github.veithen.rbeans.RBeanFactoryException;
import com.googlecode.arit.resource.ClassLoaderReference;
import com.googlecode.arit.resource.ResourceScanner;
import com.googlecode.arit.resource.ResourceType;
import com.googlecode.arit.resource.SimpleClassLoaderReference;
import com.googlecode.arit.resource.SimpleResource;

public class JavaReflectionAdapterResourceScanner implements ResourceScanner {
    private final RBeanFactory rbf;
	private final JavaReflectionAdapterStaticRBean rbean;
	private static final Log LOG = LogFactory.getLog(JavaReflectionAdapterResourceScanner.class);

	@Autowired
	@Qualifier("websphere-bug")
	private ResourceType resourceType;
    
	public JavaReflectionAdapterResourceScanner() {
		RBeanFactory rbf;
		try {
			rbf = new RBeanFactory(JavaReflectionAdapterStaticRBean.class, JavaReflectionAdapterRBean.class);
		} catch (RBeanFactoryException ex) {
			rbf = null;
		}
        this.rbf = rbf;
		this.rbean = rbf == null ? null : rbf.createRBean(JavaReflectionAdapterStaticRBean.class);
    }

	public String getDescription() {
		return "Cached JavaReflectionAdapter instances (JR40617)";
    }

	public boolean isAvailable() {
		return rbean != null;
    }

	public void scanForResources(ResourceListener resourceEventListener) {
		Map<Object, Object> cache = rbean.getAdapters();
		for (Entry<Object, Object> entry : cache.entrySet()) {
			Object key = entry.getKey();
			Object adapter = entry.getValue();
			Class<?> clazz;
			ClassLoaderReference clRef;
			// In the original JavaReflectionAdapter code, the key was a Class object
			// (causing the class loader leak). The interim fix changes this to a String.
			if (key instanceof String) {
				clazz = rbf.createRBean(JavaReflectionAdapterRBean.class, adapter).getClazz();
				clRef = new SimpleClassLoaderReference(clazz.getClassLoader(), "JavaReflectionAdapter instance");
			} else if (key instanceof Class<?>) {
				clazz = (Class<?>) key;
				clRef = new SimpleClassLoaderReference(clazz.getClassLoader(), "Cache key");
			} else {
				LOG.error("Unknown JavaReflectionAdapter cache key type encountered in JavaReflectionAdapter cache: "
						+ key.getClass().getName());
				break;
			}

			String description = "Cached JavaReflectionAdapter for class " + clazz.getName();
			SimpleResource<Object> resource = new SimpleResource<Object>(resourceType, adapter, description);
			resource.addClassloaderReference(clRef);
			resourceEventListener.onResourceFound(resource);
		}
    }
}
