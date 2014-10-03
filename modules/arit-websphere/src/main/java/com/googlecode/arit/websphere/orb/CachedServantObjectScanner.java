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

import java.util.Enumeration;
import java.util.HashSet;
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

public class CachedServantObjectScanner implements ResourceScanner, CleanerPlugin {
	private static final Log LOG = LogFactory.getLog(CachedServantObjectScanner.class);

	@Autowired
	@Qualifier("cached-servant-object")
	private ResourceType resourceType;

	private final EJSRootOAImplRBean rbean;

	public CachedServantObjectScanner() {
		ObjectResolverRBean rbean;
		try {
			rbean =
					new RBeanFactory(GlobalORBFactoryRBean.class).createRBean(GlobalORBFactoryRBean.class).globalORB()
							.getObjectResolver();
		} catch (RBeanFactoryException ex) {
			rbean = null;
		}
		this.rbean = rbean instanceof EJSRootOAImplRBean ? (EJSRootOAImplRBean) rbean : null;
    }

	public class CachedServantObject implements Resource<Object> {

		private final Object servant;

		public CachedServantObject(Object servant) {
			this.servant = servant;
		}

		public ResourceType getResourceType() {
			return resourceType;
		}

		public Object getResourceObject() {
			return servant;
		}

		public String getDescription(Formatter formatter) {
			return "Cached servant object";
		}

		public boolean isGarbageCollectable() {
			// TODO figure out if there are weak references used by WAS
			return false;
		}

		public Set<ClassLoaderReference> getClassLoaderReferences() {
			Set<ClassLoaderReference> clRefs = new HashSet<ClassLoaderReference>(1);
			clRefs.add(new SimpleClassLoaderReference(servant.getClass().getClassLoader(), "Servant: "
					+ servant.getClass().getName()));
			return clRefs;
		}

	}

	public void scanForResources(ResourceListener resourceEventListener) {
		Enumeration<ObjectImplRBean> elements = rbean.getServantObjects().elements();
		while (elements.hasMoreElements()) {
			DelegateRBean delegate = elements.nextElement().getDelegate();
			if (delegate instanceof ClientDelegateRBean) {
				Object servant = ((ClientDelegateRBean) delegate).getIOR().getServant();
				resourceEventListener.onResourceFound(new CachedServantObject(servant));
			}
		}
	}

	public boolean isAvailable() {
		return rbean != null;
	}

	public String getDescription() {
		return "Cached servant objects";
	}

	public void clean(ClassLoader classLoader) {
		Enumeration<ObjectImplRBean> e = rbean.getServantObjects().elements();
		while (e.hasMoreElements()) {
			ObjectImplRBean objectImpl = e.nextElement();
			DelegateRBean delegate = objectImpl.getDelegate();
			if (delegate instanceof ClientDelegateRBean) {
				Object servant = ((ClientDelegateRBean) delegate).getIOR().getServant();
				if (servant != null && servant.getClass().getClassLoader() == classLoader) {
					rbean.unregisterServant((org.omg.CORBA.Object) objectImpl._getTargetObject());
					LOG.info("Removed cached servant object " + servant.getClass().getName());
				}
			}
		}
	}
}
