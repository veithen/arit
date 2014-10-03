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
package com.googlecode.arit.websphere.orb;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.github.veithen.rbeans.RBeanFactory;
import com.github.veithen.rbeans.RBeanFactoryException;
import com.github.veithen.rbeans.collections.MapWrapper;
import com.googlecode.arit.Formatter;
import com.googlecode.arit.resource.ClassLoaderReference;
import com.googlecode.arit.resource.CleanerPlugin;
import com.googlecode.arit.resource.Resource;
import com.googlecode.arit.resource.ResourceScanner;
import com.googlecode.arit.resource.ResourceType;
import com.googlecode.arit.resource.SimpleClassLoaderReference;

public class WLMProxyScanner implements ResourceScanner, CleanerPlugin {
	private static final Log LOG = LogFactory.getLog(WLMProxyScanner.class);

	private final SelectionManagerRBean rbean;

	@Autowired
	@Qualifier("ws-wlm-proxy")
	private ResourceType resourceType;

	public class WLMProxy implements Resource<Object> {
		private final MasterProxyRBean proxy;
		private final Object servant;

		public WLMProxy(MasterProxyRBean proxy, Object servant) {
			this.proxy = proxy;
			this.servant = servant;
		}

		public ResourceType getResourceType() {
			return resourceType;
		}

		public Object getResourceObject() {
			return proxy._getTargetObject();
		}

		public String getDescription(Formatter formatter) {
			return "WLM proxy";
		}

		public boolean isGarbageCollectable() {
			// TODO check if weak refs are used
			return false;
		}

		public Set<ClassLoaderReference> getClassLoaderReferences() {
			Set<ClassLoaderReference> clRefs = new HashSet<ClassLoaderReference>(1);
			clRefs.add(new SimpleClassLoaderReference(servant.getClass().getClassLoader(), "Servant: "
					+ servant.getClass().getName()));
			return clRefs;
		}

    }

	public WLMProxyScanner() {
		SelectionManagerRBean rbean;
		try {
			rbean =
					((WLMClientForCommonRouterImplRBean) ((WLMClientRBean) new RBeanFactory(GlobalORBFactoryRBean.class)
							.createRBean(GlobalORBFactoryRBean.class).globalORB().getWLMPlugin()).getImpl())
							.getSelectionManager();
		} catch (RBeanFactoryException ex) {
			rbean = null;
		}
		this.rbean = rbean;
	}

	public boolean isAvailable() {
		return rbean != null;
	}

	public String getDescription() {
		return "WLM Proxies";
	}

	public void clean(ClassLoader classLoader) {
		Map<DelegateRBean, MasterProxyRBean> proxies = rbean.getProxies();
		// TODO: referring directly to MapWrapper is suboptimal
		synchronized (((MapWrapper) proxies).getTargetObject()) {
			for (Iterator<MasterProxyRBean> it = proxies.values().iterator(); it.hasNext();) {
				MasterProxyRBean proxy = it.next();
				IORRBean ior = proxy.getIOR();
				if (ior != null) {
					Object servant = ior.getServant();
					if (servant != null && servant.getClass().getClassLoader() == classLoader) {
						it.remove();
						LOG.info("Removed WLM master proxy for servant " + servant.getClass().getName());
					}
				}
			}
		}
	}

	public void scanForResources(ResourceListener resourceEventListener) {
		for (MasterProxyRBean proxy : rbean.getProxies().values()) {
			if (proxy.getIOR() != null) {
				Object servant = proxy.getIOR().getServant();
				if (servant != null) {
					resourceEventListener.onResourceFound(new WLMProxy(proxy, servant));
				}
			}
		}
	}
}
