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
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

/**
 * Interface implemented by Plexus components that expose an MBean.
 */
public interface MBeanProvider {
    /**
     * Register the MBean provided by the implementation.
     * 
     * @param server
     *            the MBean server where the MBean should be registered
     * @param name
     *            the object name to use; this name is calculated based on the
     *            hint
     * @return the object instance returned by the MBean server during
     *         registration
     * @throws JMException
     *             if the MBean registration fails
     */
    ObjectInstance registerMBean(MBeanServer server, ObjectName name) throws JMException;
}
