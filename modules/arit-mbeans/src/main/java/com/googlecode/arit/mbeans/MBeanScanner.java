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
package com.googlecode.arit.mbeans;

import java.util.ArrayList;
import java.util.Set;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.googlecode.arit.Messages;
import com.googlecode.arit.resource.CleanerPlugin;
import com.googlecode.arit.resource.ResourceScanner;
import com.googlecode.arit.resource.ResourceType;
import com.googlecode.arit.resource.SimpleResource;

public class MBeanScanner implements ResourceScanner, CleanerPlugin {

	private static final Log LOG = LogFactory.getLog(MBeanScanner.class);

	@Autowired
	@Qualifier("mbean")
	private ResourceType resourceType;

	@Autowired
	private MBeanServerInspector mbsInspector;

	@Autowired(required = false)
	private Messages messages;

	public boolean isAvailable() {
		return mbsInspector.isAvailable();
	}

	public String getDescription() {
		return "MBeans";
	}

	public void scanForResources(ResourceListener resourceEventListener) {
		ArrayList<MBeanServer> mbeanServers = MBeanServerFactory.findMBeanServer(null);
		for (MBeanServer mbs : mbeanServers) {
			MBeanAccessor mbeanAccessor = mbsInspector.inspect(mbs);
			if (mbeanAccessor == null) {
				String errorMsg = "Unable to inspect MBeanServer of type " + mbs.getClass().getName();
				LOG.error(errorMsg);
				if (messages != null) {
					messages.addMessage(errorMsg);
				}
			} else {
				Set<ObjectName> mbeans;
				try {
					mbeans= mbs.queryNames(new ObjectName("*:*"), null);
				} catch (MalformedObjectNameException ex) {
					// We should never get here
					throw new Error(ex);
				}
				
				for(ObjectName name : mbeans) {
	                Object mbean = mbeanAccessor.retrieve(name);
					SimpleResource<Object> mbeanResource = new SimpleResource<Object>(resourceType, mbean, "MBean: " + name);
					mbeanResource.addClassloaderReference(mbean.getClass().getClassLoader(), "Implementation class: " + mbean.getClass().getName());
					resourceEventListener.onResourceFound(mbeanResource);
				}
			}
		}
	}


	public void clean(ClassLoader classLoader) {
		ArrayList<MBeanServer> mbeanServers = MBeanServerFactory.findMBeanServer(null);
		for (MBeanServer mbs : mbeanServers) {
			MBeanAccessor mbeanAccessor = mbsInspector.inspect(mbs);
			if (mbeanAccessor == null) {
				String errorMsg = "Unable to inspect MBeanServer of type " + mbs.getClass().getName();
				LOG.error(errorMsg);
				if (messages != null) {
					messages.addMessage(errorMsg);
				}
			} else {
				Set<ObjectName> mbeans;
				try {
					mbeans= mbs.queryNames(new ObjectName("*:*"), null);
				} catch (MalformedObjectNameException ex) {
					// We should never get here
					throw new Error(ex);
				}
				
				for(ObjectName name : mbeans) {
	                Object mbean = mbeanAccessor.retrieve(name);
	                if(mbean.getClass().getClassLoader().equals(classLoader)) {
	                	  try {
	                          mbs.unregisterMBean(name);
	                      } catch (JMException ex) {
	                    	  String errorMsg = "Error during cleanup of mbean - unregistration of mbean " + name.toString() + " failed";
	                    	  LOG.error(errorMsg, ex);
	                    	  if (messages != null) {
	          					messages.addMessage(errorMsg);
	          				  }
	                      }
	                }
				}
			}
		}
	}
}
