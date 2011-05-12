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

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

import com.googlecode.arit.ResourceEnumerator;
import com.googlecode.arit.ResourceEnumeratorFactory;
import com.googlecode.arit.ResourceType;
import com.googlecode.arit.rbeans.RBeanFactory;
import com.googlecode.arit.rbeans.RBeanFactoryException;

@Component(role=ResourceEnumeratorFactory.class, hint="PM21638")
public class PM21638ResourceEnumeratorFactory implements ResourceEnumeratorFactory {
    private final BeanELResolverRBean rbean;
    
    @Requirement(hint="websphere-bug")
    private ResourceType resourceType;
    
    public PM21638ResourceEnumeratorFactory() {
        BeanELResolverRBean rbean;
        try {
            rbean = new RBeanFactory(BeanELResolverRBean.class).createRBean(BeanELResolverRBean.class);
        } catch (RBeanFactoryException ex) {
            rbean = null;
        }
        this.rbean = rbean;
    }

    public String getDescription() {
        return "Cached bean property descriptors in javax.el.BeanELResolver (PM21638)";
    }

    public boolean isAvailable() {
        return rbean != null;
    }

    public ResourceEnumerator createEnumerator() {
        return new PM21638ResourceEnumerator(resourceType, rbean.getProperties());
    }
}
