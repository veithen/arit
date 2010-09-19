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

import javax.management.ObjectName;
import javax.management.modelmbean.RequiredModelMBean;

import com.googlecode.arit.rbeans.RBeanFactory;
import com.sun.jmx.mbeanserver.DynamicMBean2;
import com.sun.jmx.mbeanserver.Repository;

public class SunJava6MBeanRepository implements MBeanAccessor {
    private final Repository repository;
    private final RBeanFactory rbf;

    public SunJava6MBeanRepository(Repository repository, RBeanFactory rbf) {
        this.repository = repository;
        this.rbf = rbf;
    }

    public Object retrieve(ObjectName name) {
        Object object = repository.retrieve(name);
        if (object instanceof DynamicMBean2) {
            return ((DynamicMBean2)object).getResource();
        } else if (object instanceof RequiredModelMBean) {
            // TODO: also implement this for Java 5
            return rbf.createRBean(RequiredModelMBeanRBean.class, object).getManagedResource();
        } else {
            return object;
        }
    }
}
