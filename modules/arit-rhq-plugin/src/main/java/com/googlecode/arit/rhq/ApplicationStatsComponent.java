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

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mc4j.ems.connection.EmsInvocationException;
import org.mc4j.ems.connection.bean.EmsBean;
import org.rhq.core.domain.measurement.AvailabilityType;
import org.rhq.core.domain.measurement.MeasurementDataNumeric;
import org.rhq.core.domain.measurement.MeasurementReport;
import org.rhq.core.domain.measurement.MeasurementScheduleRequest;
import org.rhq.core.pluginapi.inventory.InvalidPluginConfigurationException;
import org.rhq.core.pluginapi.inventory.ResourceComponent;
import org.rhq.core.pluginapi.inventory.ResourceContext;
import org.rhq.core.pluginapi.measurement.MeasurementFacet;
import org.rhq.plugins.jmx.MBeanResourceComponent;

public class ApplicationStatsComponent implements ResourceComponent<MBeanResourceComponent<?>>, MeasurementFacet {
    private static final Log log = LogFactory.getLog(ApplicationStatsComponent.class);
    
    private EmsBean bean;
    private String moduleName;

    public void start(ResourceContext<MBeanResourceComponent<?>> context) throws InvalidPluginConfigurationException, Exception {
        bean = context.getParentResourceComponent().getEmsBean();
        moduleName = context.getResourceKey();
    }

    public AvailabilityType getAvailability() {
        return AvailabilityType.UP;
    }

    public void getValues(MeasurementReport report, Set<MeasurementScheduleRequest> requests) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("Entering getValues; moduleName=" + moduleName);
        }
        for (MeasurementScheduleRequest request : requests) {
            if (request.getName().equals("detectedLeakCount")) {
                int detectedLeakCount;
                try {
                    detectedLeakCount = (Integer)bean.getOperation("getDetectedLeakCount", String.class).invoke(moduleName);
                } catch (EmsInvocationException ex) {
                    if (log.isDebugEnabled()) {
                        log.debug("Invocation of getDetectedLeakCount failed", ex);
                    }
                    throw ex;
                }
                if (log.isDebugEnabled()) {
                    log.debug("Adding value for detectedLeakCount=" + detectedLeakCount);
                }
                report.addData(new MeasurementDataNumeric(request, (double)detectedLeakCount));
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Exiting getValues; moduleName=" + moduleName);
        }
    }

    public void stop() {
    }
}
