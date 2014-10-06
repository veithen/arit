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

import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.github.veithen.rbeans.RBeanFactory;
import com.github.veithen.rbeans.RBeanFactoryException;
import com.googlecode.arit.resource.ResourceScanner;
import com.googlecode.arit.resource.ResourceType;
import com.googlecode.arit.resource.SimpleResource;

public class PM21638ResourceScanner implements ResourceScanner {
    private final BeanELResolverRBean rbean;
    
    @Autowired
    @Qualifier("websphere-bug")
    private ResourceType resourceType;
    
    public PM21638ResourceScanner() {
        BeanELResolverRBean rbean;
        try {
            rbean = new RBeanFactory(BeanELResolverRBean.class).createRBean(BeanELResolverRBean.class);
        } catch (RBeanFactoryException ex) {
            rbean = null;
        }
        this.rbean = rbean;
    }

    public String getDescription() {
        return "Cached bean property descriptors in javax.el.BeanELResolver (PM21638/PM38747)";
    }

    public boolean isAvailable() {
        return rbean != null;
    }

	public void scanForResources(ResourceListener resourceEventListener) {
		for (Class<?> clazz : rbean.getProperties().keySet()) {
			SimpleResource<Class<?>> resource =
					new SimpleResource<Class<?>>(resourceType, clazz,
							"PM21638/PM38747: Cached bean property descriptors for class " + clazz.getName() + clazz.getName());
			resource.addClassloaderReference(clazz.getClassLoader(), "Cache key");
			resourceEventListener.onResourceFound(resource);
		}

	}

	public void clean(ClassLoader classLoader) {
		Iterator<Class<?>> classIterator = rbean.getProperties().keySet().iterator();
		while (classIterator.hasNext()) {
			Class<?> clazz = classIterator.next();
			if (clazz.getClassLoader().equals(classLoader)) {
				classIterator.remove();
			}
		}
	}

}
