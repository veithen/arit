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
package com.googlecode.arit.websphere.iz67457;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

import com.googlecode.arit.ResourceEnumerator;
import com.googlecode.arit.ResourceEnumeratorFactory;
import com.googlecode.arit.ResourceType;
import com.googlecode.arit.rbeans.RBeanFactory;
import com.googlecode.arit.rbeans.RBeanFactoryException;

// TODO: the IZ67457 issue may actually come from Apache Harmony; check this and if necessary move the code out of the WebSphere module
@Component(role=ResourceEnumeratorFactory.class, hint="iz67457")
public class IZ67457ResourceEnumeratorFactory implements ResourceEnumeratorFactory {
    private final StandardBeanInfoRBean rbean;
    
    @Requirement(hint="iz67457")
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

    public ResourceEnumerator createEnumerator() {
        return new IZ67457ResourceEnumerator(resourceType, rbean.getMethodDescriptorCache().keySet().iterator());
    }
}
