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
package com.googlecode.arit.jdbc;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.googlecode.arit.Formatter;
import com.googlecode.arit.resource.ClassLoaderReference;
import com.googlecode.arit.resource.Resource;
import com.googlecode.arit.resource.ResourceScanner;
import com.googlecode.arit.resource.ResourceType;
import com.googlecode.arit.resource.SimpleClassLoaderReference;

public class JdbcDriverScanner implements ResourceScanner {

	@Autowired
	@Qualifier("jdbc-driver")
	private ResourceType resourceType;

	@Autowired
	private DriverManagerInspector inspector;
    
	public boolean isAvailable() {
		return inspector.isAvailable();
    }

	public String getDescription() {
		return "JDBC drivers";
    }

	public class DriverClassResource implements Resource<Class<?>> {

		private final Class<?> driverClass;

		public DriverClassResource(Class<?> driverClass) {
			this.driverClass = driverClass;
		}

		public ResourceType getResourceType() {
			return resourceType;
		}

		public Class<?> getResourceObject() {
			// TODO: this is probably not correct
			return driverClass;
		}

		public String getDescription(Formatter formatter) {
			return "JDBC driver: " + driverClass.getName();
		}

		public boolean isGarbageCollectable() {
			// TODO check whether garbage collectable
			return false;
		}

		public Set<ClassLoaderReference> getClassLoaderReferences() {
			Set<ClassLoaderReference> clRefs = new HashSet<ClassLoaderReference>(1);
			clRefs.add(new SimpleClassLoaderReference(driverClass.getClassLoader(), "Driver class"));
			return clRefs;
		}
    }

	public void scanForResources(ResourceListener resourceEventListener) {
		// TODO Auto-generated method stub
		List<Class<?>> driverClasses = inspector.getDriverClasses();
		for (Class<?> driverClass : driverClasses) {
			resourceEventListener.onResourceFound(new DriverClassResource(driverClass));
		}

	}
}
