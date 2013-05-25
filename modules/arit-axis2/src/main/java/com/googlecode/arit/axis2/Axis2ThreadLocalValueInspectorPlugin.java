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
package com.googlecode.arit.axis2;

import java.util.Set;

import com.github.veithen.rbeans.RBeanFactory;
import com.github.veithen.rbeans.RBeanFactoryException;
import com.googlecode.arit.threadlocals.ThreadLocalValueDescription;
import com.googlecode.arit.threadlocals.ThreadLocalValueInspectorPlugin;

public class Axis2ThreadLocalValueInspectorPlugin implements ThreadLocalValueInspectorPlugin {
    private final RBeanFactory rbf;
    private final Class<?> axisServiceClass;
    
    public Axis2ThreadLocalValueInspectorPlugin() {
        RBeanFactory rbf;
        try {
            rbf = new RBeanFactory(AxisServiceRBean.class);
        } catch (RBeanFactoryException ex) {
            rbf = null;
        }
        this.rbf = rbf;
        axisServiceClass = rbf == null ? null : rbf.getRBeanInfo(AxisServiceRBean.class).getTargetClass();
    }
    
    public boolean isAvailable() {
        return rbf != null;
    }

    public void identify(Set<ThreadLocalValueDescription> descriptions, Object object) {
        if (axisServiceClass.isInstance(object)) {
            AxisServiceRBean axisService = rbf.createRBean(AxisServiceRBean.class, object);
            descriptions.add(new AxisServiceThreadLocalValueDescription(axisService.getName(), axisService.getClassLoader()));
        }
    }
}
