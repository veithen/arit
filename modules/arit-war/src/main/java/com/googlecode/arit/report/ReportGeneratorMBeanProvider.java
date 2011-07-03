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
package com.googlecode.arit.report;

import javax.management.JMException;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.modelmbean.InvalidTargetObjectTypeException;
import javax.management.modelmbean.ModelMBeanAttributeInfo;
import javax.management.modelmbean.ModelMBeanConstructorInfo;
import javax.management.modelmbean.ModelMBeanInfoSupport;
import javax.management.modelmbean.ModelMBeanNotificationInfo;
import javax.management.modelmbean.ModelMBeanOperationInfo;
import javax.management.modelmbean.RequiredModelMBean;

import org.springframework.beans.factory.annotation.Autowired;

import com.googlecode.arit.jmx.MBeanProvider;

public class ReportGeneratorMBeanProvider implements MBeanProvider {
    @Autowired
    private ReportGenerator reportGenerator;

    public ObjectInstance registerMBean(MBeanServer server, ObjectName name) throws JMException {
        RequiredModelMBean mbean = new RequiredModelMBean();
        mbean.setModelMBeanInfo(new ModelMBeanInfoSupport(
                ReportGenerator.class.getName(),
                "Generates Arit reports",
                new ModelMBeanAttributeInfo[0],
                new ModelMBeanConstructorInfo[0],
                new ModelMBeanOperationInfo[] {
                        new ModelMBeanOperationInfo(
                                "generateReport",
                                "Generate an Arit report",
                                new MBeanParameterInfo[0],
                                Report.class.getName(),
                                ModelMBeanOperationInfo.INFO) },
                new ModelMBeanNotificationInfo[0]));
        try {
            mbean.setManagedResource(reportGenerator, "ObjectReference");
        } catch (InvalidTargetObjectTypeException ex) {
            // Should never happen
            throw new RuntimeException(ex);
        }
        return server.registerMBean(mbean, name);
    }
}
