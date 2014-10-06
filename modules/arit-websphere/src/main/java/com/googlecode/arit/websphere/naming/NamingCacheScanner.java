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
package com.googlecode.arit.websphere.naming;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.github.veithen.rbeans.RBeanFactory;
import com.github.veithen.rbeans.RBeanFactoryException;
import com.googlecode.arit.Formatter;
import com.googlecode.arit.resource.ClassLoaderReference;
import com.googlecode.arit.resource.Resource;
import com.googlecode.arit.resource.ResourceScanner;
import com.googlecode.arit.resource.ResourceType;
import com.googlecode.arit.resource.SimpleClassLoaderReference;
import com.googlecode.arit.websphere.orb.ClientDelegateRBean;
import com.googlecode.arit.websphere.orb.DelegateRBean;
import com.googlecode.arit.websphere.orb.ObjectImplRBean;

public class NamingCacheScanner implements ResourceScanner {
	private static final Log LOG = LogFactory.getLog(NamingCacheScanner.class);

	private final CacheManagerRBean rbean;

	@Autowired
	@Qualifier("ws-naming-cache")
	private ResourceType resourceType;

	public NamingCacheScanner() {
		CacheManagerRBean rbean;
		try {
			rbean = new RBeanFactory(CacheManagerRBean.class).createRBean(CacheManagerRBean.class);
		} catch (RBeanFactoryException ex) {
			rbean = null;
		}
		this.rbean = rbean;
	}

	public boolean isAvailable() {
		return rbean != null;
	}

	public String getDescription() {
		return "Cached JNDI lookups";
	}
    
	public void scanForResources(ResourceListener resourceEventListener) {
		Map<CacheKeyRBean, CacheRBean> caches = rbean.getCaches();
		if (LOG.isDebugEnabled()) {
			LOG.debug("Created " + NamingCacheScanner.class.getSimpleName() + "; number of caches: " + caches.size());
		}

		for (CacheRBean cache : caches.values()) {
			ClassLoader classLoader = cache.getClassLoader();
			Map<BindingsTableKeyRBean, BindingsTableDataRBean> bindingsTable = cache.getCache().getBindingsTable();
			if (LOG.isDebugEnabled()) {
				LOG.debug("Starting to scan cache; name: " + cache.getCacheName() + "; classLoader: "
						+ cache.getClassLoader().getClass().getName() + "; bindingsTable size: " + bindingsTable.size());
			}
			for (BindingsTableDataRBean data : bindingsTable.values()) {
				Object servant = null;

				Object object = data.getObject();
				if (object instanceof ObjectImplRBean) {
					DelegateRBean delegate = ((ObjectImplRBean) object).getDelegate();
					if (delegate instanceof ClientDelegateRBean) {
						servant = ((ClientDelegateRBean) delegate).getIOR().getServant();
					}
				}

				if (LOG.isDebugEnabled()) {
					LOG.debug("Got next entry; name: " + data.getName() + "; type: " + object.getClass().getName()
							+ "; servant: " + (servant == null ? null : servant.getClass().getName()));
				}
				resourceEventListener.onResourceFound(new NamingCacheEntryResource(data, servant, classLoader));
			}
		}
    }

	public class NamingCacheEntryResource implements Resource<Object> {

		private final BindingsTableDataRBean data;
		private final Object servant;
		private final ClassLoader classLoader;

		public NamingCacheEntryResource(BindingsTableDataRBean data, Object servant, ClassLoader classLoader) {
			this.data = data;
			this.servant = servant;
			this.classLoader = classLoader;
		}

		public Object getResourceObject() {
			return data._getTargetObject();
		}

		public ResourceType getResourceType() {
			return resourceType;
		}

		public String getDescription(Formatter formatter) {
			return "Cached JNDI lookup for " + data.getName();
		}

		public Set<ClassLoaderReference> getClassLoaderReferences() {
			Set<ClassLoaderReference> clRefs = new HashSet<ClassLoaderReference>();
			clRefs.add(new SimpleClassLoaderReference(classLoader, "Context class loader"));
			if (servant != null) {
				clRefs.add(new SimpleClassLoaderReference(servant.getClass().getClassLoader(), "Servant: "
						+ servant.getClass().getName()));
			}
			return clRefs;
		}

		public boolean isGarbageCollectable() {
			// TODO Auto-generated method stub
			return false;
		}

    }
}
