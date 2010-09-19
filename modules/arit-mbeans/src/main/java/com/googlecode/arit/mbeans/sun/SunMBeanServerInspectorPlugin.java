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
package com.googlecode.arit.mbeans.sun;

import javax.management.MBeanServer;

import com.googlecode.arit.mbeans.MBeanAccessor;
import com.googlecode.arit.mbeans.MBeanServerInspectorPlugin;
import com.googlecode.arit.rbeans.RBeanFactory;
import com.googlecode.arit.rbeans.RBeanFactoryException;

public abstract class SunMBeanServerInspectorPlugin<T extends MBeanServerInterceptorRBean> implements MBeanServerInspectorPlugin {
    private final Class<T> defaultMBeanServerInterceptorClass;
    // TODO: should be private
    protected final RBeanFactory rbf;
    
    public SunMBeanServerInspectorPlugin(Class<T> defaultMBeanServerInterceptorClass) {
        this.defaultMBeanServerInterceptorClass = defaultMBeanServerInterceptorClass;
        RBeanFactory rbf;
        try {
            rbf = new RBeanFactory(JmxMBeanServerRBean.class, RequiredModelMBeanRBean.class, defaultMBeanServerInterceptorClass);
        } catch (RBeanFactoryException ex) {
            rbf = null;
        }
        this.rbf = rbf;
    }

    public boolean isAvailable() {
        return rbf != null;
    }

    public MBeanAccessor inspect(MBeanServer mbs) {
        if (rbf.getRBeanInfo(JmxMBeanServerRBean.class).getTargetClass().isInstance(mbs)) {
            MBeanServerInterceptorRBean interceptor = rbf.createRBean(JmxMBeanServerRBean.class, mbs).getInterceptor();
            return createAccessor(defaultMBeanServerInterceptorClass.cast(interceptor));
        } else {
            return null;
        }
    }
    
    protected abstract MBeanAccessor createAccessor(T defaultMBeanServerInterceptorRBean);
}
