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
package com.googlecode.arit.websphere.bug;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.github.veithen.rbeans.RBeanFactory;
import com.github.veithen.rbeans.RBeanFactoryException;
import com.googlecode.arit.Logger;
import com.googlecode.arit.resource.ResourceEnumeratorFactory;
import com.googlecode.arit.resource.ResourceType;

// TODO: the IZ67457 issue may actually come from Apache Harmony; check this and if necessary move the code out of the WebSphere module
public class IZ67457ResourceEnumeratorFactory implements ResourceEnumeratorFactory<IZ67457ResourceEnumerator> {
    private final StandardBeanInfoRBean rbean;
    
    @Autowired
    @Qualifier("websphere-bug")
    private ResourceType resourceType;
    
    public IZ67457ResourceEnumeratorFactory() {
        StandardBeanInfoRBean rbean;
        try {
            rbean = new RBeanFactory(StandardBeanInfoRBean.class).createRBean(StandardBeanInfoRBean.class);
        } catch (RBeanFactoryException ex) {
            rbean = null;
        }
        this.rbean = rbean;
    }

    public String getDescription() {
        return "Cached MethodDescriptor array in StandardBeanInfo (IZ67457)";
    }

    public boolean isAvailable() {
        return rbean != null;
    }

    public IZ67457ResourceEnumerator createEnumerator(Logger logger) {
        return new IZ67457ResourceEnumerator(resourceType, rbean.getMethodDescriptorCache());
    }
}
