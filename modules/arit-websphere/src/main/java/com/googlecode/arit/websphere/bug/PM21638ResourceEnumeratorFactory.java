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

import com.googlecode.arit.ResourceEnumeratorFactory;
import com.googlecode.arit.ResourceType;
import com.googlecode.arit.rbeans.RBeanFactory;
import com.googlecode.arit.rbeans.RBeanFactoryException;

public class PM21638ResourceEnumeratorFactory implements ResourceEnumeratorFactory<PM21638ResourceEnumerator> {
    private final BeanELResolverRBean rbean;
    
    @Autowired
    @Qualifier("websphere-bug")
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
        return "Cached bean property descriptors in javax.el.BeanELResolver (PM21638/PM38747)";
    }

    public boolean isAvailable() {
        return rbean != null;
    }

    public PM21638ResourceEnumerator createEnumerator() {
        return new PM21638ResourceEnumerator(resourceType, rbean.getProperties());
    }
}
