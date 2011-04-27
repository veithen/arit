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
package com.googlecode.arit.jmx;

import javax.management.JMException;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.modelmbean.DescriptorSupport;
import javax.management.modelmbean.InvalidTargetObjectTypeException;
import javax.management.modelmbean.ModelMBeanAttributeInfo;
import javax.management.modelmbean.ModelMBeanConstructorInfo;
import javax.management.modelmbean.ModelMBeanInfoSupport;
import javax.management.modelmbean.ModelMBeanOperationInfo;
import javax.management.modelmbean.RequiredModelMBean;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

@Component(role=MBeanProvider.class, hint="LeakDetector")
public class LeakDetectorMBeanProvider implements MBeanProvider {
    @Requirement
    private LeakDetector leakDetector;
    
    public ObjectInstance registerMBean(MBeanServer server, ObjectName name) throws JMException {
        RequiredModelMBean mbean = new RequiredModelMBean();
        mbean.setModelMBeanInfo(new ModelMBeanInfoSupport(
                LeakDetector.class.getName(),
                "Detects applications with memory leaks",
                new ModelMBeanAttributeInfo[] {
                        new ModelMBeanAttributeInfo(
                                "detectedLeakCount",
                                "int",
                                "The number of stopped application instances with memory leaks",
                                true,
                                false,
                                false,
                                new DescriptorSupport(new String[] {
                                        "name=detectedLeakCount",
                                        "descriptorType=attribute",
                                        "getMethod=getDetectedLeakCount"}))
                },
                new ModelMBeanConstructorInfo[0],
                new ModelMBeanOperationInfo[] {
                        new ModelMBeanOperationInfo(
                                "getDetectedLeakCount",
                                "Get the number of stopped application instances with memory leaks",
                                new MBeanParameterInfo[0],
                                "int",
                                ModelMBeanOperationInfo.INFO)
                },
                leakDetector.getNotificationInfo()));
        try {
            mbean.setManagedResource(leakDetector, "ObjectReference");
        } catch (InvalidTargetObjectTypeException ex) {
            // Should never happen
            throw new RuntimeException(ex);
        }
        return server.registerMBean(mbean, name);
    }
}
