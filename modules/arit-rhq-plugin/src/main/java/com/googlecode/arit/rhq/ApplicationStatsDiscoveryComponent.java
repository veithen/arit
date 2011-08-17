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
package com.googlecode.arit.rhq;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mc4j.ems.connection.bean.EmsBean;
import org.rhq.core.pluginapi.inventory.DiscoveredResourceDetails;
import org.rhq.core.pluginapi.inventory.InvalidPluginConfigurationException;
import org.rhq.core.pluginapi.inventory.ResourceDiscoveryComponent;
import org.rhq.core.pluginapi.inventory.ResourceDiscoveryContext;
import org.rhq.plugins.jmx.MBeanResourceComponent;

public class ApplicationStatsDiscoveryComponent implements ResourceDiscoveryComponent<MBeanResourceComponent<?>> {
    private static final Log log = LogFactory.getLog(ApplicationStatsDiscoveryComponent.class);
    
    public Set<DiscoveredResourceDetails> discoverResources(ResourceDiscoveryContext<MBeanResourceComponent<?>> context) throws InvalidPluginConfigurationException, Exception {
        EmsBean bean = context.getParentResourceComponent().getEmsBean();
        Set<DiscoveredResourceDetails> result = new HashSet<DiscoveredResourceDetails>();
        String[] moduleNames = (String[])bean.refreshAttributes(Collections.singletonList("moduleNames")).get(0).getValue();
        if (log.isDebugEnabled()) {
            log.debug("Discovered the following application modules: " + Arrays.asList(moduleNames));
        }
        for (String moduleName : moduleNames) {
            result.add(new DiscoveredResourceDetails(context.getResourceType(), moduleName, moduleName, null, "Application statistics", null, null));
        }
        return result;
    }
}
