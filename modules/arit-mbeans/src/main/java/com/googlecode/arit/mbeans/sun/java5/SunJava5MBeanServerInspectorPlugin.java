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
package com.googlecode.arit.mbeans.sun.java5;

import com.googlecode.arit.mbeans.MBeanAccessor;
import com.googlecode.arit.mbeans.sun.SunMBeanServerInspectorPlugin;

public class SunJava5MBeanServerInspectorPlugin extends SunMBeanServerInspectorPlugin<DefaultMBeanServerInterceptorRBean> {
    public SunJava5MBeanServerInspectorPlugin() {
        super(DefaultMBeanServerInterceptorRBean.class);
    }

    @Override
    public boolean isAvailable() {
        // TODO: clean this up; the RBeanFactory creation should fail if there is an API mismatch
        return super.isAvailable() && System.getProperty("java.version").startsWith("1.5");
    }

    @Override
    protected MBeanAccessor createAccessor(DefaultMBeanServerInterceptorRBean defaultMBeanServerInterceptorRBean) {
        return new SunJava5MBeanAccessor(defaultMBeanServerInterceptorRBean.getRepository());
    }
}
