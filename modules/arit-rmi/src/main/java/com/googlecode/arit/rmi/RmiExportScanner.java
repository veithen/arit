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
package com.googlecode.arit.rmi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.googlecode.arit.plugin.SingletonPluginManager;
import com.googlecode.arit.resource.ResourceScanner;
import com.googlecode.arit.resource.ResourceType;
import com.googlecode.arit.resource.SimpleResource;

public class RmiExportScanner extends SingletonPluginManager<RmiExportScannerPlugin> implements
		ResourceScanner {
    @Autowired
    @Qualifier("rmi-export")
	private ResourceType resourceType;

    public RmiExportScanner() {
        super(RmiExportScannerPlugin.class);
    }
    
    public String getDescription() {
        return "RMI exports";
    }

	public void scanForResources(ResourceListener resourceEventListener) {
		for (Object exportedObject : getPlugin().getExportedObjects()) {
			SimpleResource<Object> resource =
					new SimpleResource<Object>(resourceType, exportedObject, "Exported object (RMI)");
			resource.addClassloaderReference(exportedObject.getClass().getClassLoader(), "Implementation class: "
					+ exportedObject.getClass().getName());
			resourceEventListener.onResourceFound(resource);
		}
    }

}
