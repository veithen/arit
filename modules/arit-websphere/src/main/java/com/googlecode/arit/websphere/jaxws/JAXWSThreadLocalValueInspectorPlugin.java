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
package com.googlecode.arit.websphere.jaxws;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.veithen.rbeans.RBeanFactory;
import com.github.veithen.rbeans.RBeanFactoryException;
import com.googlecode.arit.axis2.AxisServiceRBean;
import com.googlecode.arit.axis2.AxisServiceThreadLocalValueDescription;
import com.googlecode.arit.threadlocals.ThreadLocalValueDescription;
import com.googlecode.arit.threadlocals.ThreadLocalValueInspectorPlugin;

public class JAXWSThreadLocalValueInspectorPlugin implements ThreadLocalValueInspectorPlugin {
    private static final Log log = LogFactory.getLog(JAXWSThreadLocalValueInspectorPlugin.class);
    
    private final RBeanFactory rbf;
    private final Class<?> endpointDataClass;
    
    public JAXWSThreadLocalValueInspectorPlugin() {
        RBeanFactory rbf;
        try {
            rbf = new RBeanFactory(JAXWSThreadLocalValueInspectorPlugin.class.getClassLoader().loadClass("com.ibm.ws.was.fp.bundle.WSFPBundleActivator").getClassLoader(), EndpointDataRBean.class);
        } catch (ClassNotFoundException ex) {
            if (log.isDebugEnabled()) {
                log.debug(JAXWSThreadLocalValueInspectorPlugin.class.getName() + " disabled", ex);
            }
            rbf = null;
        } catch (RBeanFactoryException ex) {
            if (log.isDebugEnabled()) {
                log.debug(JAXWSThreadLocalValueInspectorPlugin.class.getName() + " disabled", ex);
            }
            rbf = null;
        }
        this.rbf = rbf;
        endpointDataClass = rbf == null ? null : rbf.getRBeanInfo(EndpointDataRBean.class).getTargetClass();
        if (rbf != null && log.isDebugEnabled()) {
            log.debug(JAXWSThreadLocalValueInspectorPlugin.class.getName() + " enabled; endpointDataClass=" + endpointDataClass.getName());
        }
    }
    
    public boolean isAvailable() {
        return rbf != null;
    }

    public void identify(Set<ThreadLocalValueDescription> descriptions, Object object) {
        if (endpointDataClass.isInstance(object)) {
            AxisServiceRBean axisService = rbf.createRBean(EndpointDataRBean.class, object).getAxisService();
            if (log.isDebugEnabled()) {
                log.debug("Identified EndpointData object; AxisService name: " + axisService.getName());
            }
            descriptions.add(new AxisServiceThreadLocalValueDescription(axisService.getName(), axisService.getClassLoader()));
        }
    }
}
