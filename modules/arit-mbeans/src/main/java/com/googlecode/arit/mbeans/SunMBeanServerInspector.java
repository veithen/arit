/*
 * Copyright 2010 Andreas Veithen
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

import javax.management.MBeanServer;

import com.googlecode.arit.rbeans.RBeanFactory;
import com.sun.jmx.mbeanserver.Repository;

public class SunMBeanServerInspector implements MBeanServerInspector {
    private final RBeanFactory rbf;
    private final boolean isJava6;
    
    public SunMBeanServerInspector(RBeanFactory rbf, boolean isJava6) {
        this.rbf = rbf;
        this.isJava6 = isJava6;
    }

    public MBeanRepository inspect(MBeanServer mbs) {
        if (rbf.getRBeanInfo(JmxMBeanServerRBean.class).getTargetClass().isInstance(mbs)) {
            MBeanServerInterceptorRBean interceptor = rbf.createRBean(JmxMBeanServerRBean.class, mbs).getInterceptor();
            Repository repository = (Repository)((DefaultMBeanServerInterceptorRBean)interceptor).getRepository();
            return isJava6 ? new SunJava6MBeanRepository(repository) : new SunJava5MBeanRepository(repository);
        } else {
            return null;
        }
    }
}
