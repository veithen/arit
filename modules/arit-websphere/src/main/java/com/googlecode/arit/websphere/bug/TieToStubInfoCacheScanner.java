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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.github.veithen.rbeans.RBeanFactory;
import com.github.veithen.rbeans.RBeanFactoryException;
import com.googlecode.arit.resource.ResourceScanner;
import com.googlecode.arit.resource.ResourceType;
import com.googlecode.arit.resource.SimpleResource;

public class TieToStubInfoCacheScanner implements ResourceScanner {
    private final RBeanFactory rbf;
	private final TieToStubInfoCacheRBean rbean;

	@Autowired
	@Qualifier("ws-tie-to-stub-info-cache")
	private ResourceType resourceType;

	public TieToStubInfoCacheScanner() {
		RBeanFactory rbf;
		try {
			rbf = new RBeanFactory(TieToStubInfoCacheRBean.class, DataValueListEntryRBean.class, StubInfoRBean.class);
		} catch (RBeanFactoryException ex) {
			rbf = null;
		}
        this.rbf = rbf;
		this.rbean = rbf == null ? null : rbf.createRBean(TieToStubInfoCacheRBean.class);
	}

	public String getDescription() {
		return "Tie to stub info cache entry";
	}

	public boolean isAvailable() {
		return rbean != null;
	}

	public void scanForResources(ResourceListener resourceEventListener) {
		Map<?, ?>[] maps = rbean.getMap().getMaps();
		for (Map<?, ?> map : maps) {
			for (Object value : map.values()) {
				Object next = value;
				while (next != null) {
					DataValueListEntryRBean listEntry = rbf.createRBean(DataValueListEntryRBean.class, next);
                    if (listEntry != null) {
						Object tie = listEntry.getKey();
						Object stub = rbf.createRBean(StubInfoRBean.class, listEntry.getData()).getStub();
						// Apparently, in some rare cases, the result of getStub is null
						if (stub != null) {
							SimpleResource<Object> resource =
									new SimpleResource<Object>(resourceType, tie, "Tie to stub info cache entry");
							resource.addClassloaderReference(tie.getClass().getClassLoader(), "Tie class: " + tie.getClass().getName());
							resource.addClassloaderReference(stub.getClass().getClassLoader(), "Stub class: " + stub.getClass().getName());

							resourceEventListener.onResourceFound(resource);
						}
                    }
					next = listEntry.getNext();
                }
			}
		}

    }

}
