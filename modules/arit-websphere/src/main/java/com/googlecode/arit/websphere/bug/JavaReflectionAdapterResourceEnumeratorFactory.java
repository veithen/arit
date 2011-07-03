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

import javax.annotation.Resource;

import com.googlecode.arit.ResourceEnumeratorFactory;
import com.googlecode.arit.ResourceType;
import com.googlecode.arit.rbeans.RBeanFactory;
import com.googlecode.arit.rbeans.RBeanFactoryException;

public class JavaReflectionAdapterResourceEnumeratorFactory implements ResourceEnumeratorFactory<JavaReflectionAdapterResourceEnumerator> {
    private final JavaReflectionAdapterRBean rbean;
    
    @Resource(name="websphere-bug")
    private ResourceType resourceType;
    
    public JavaReflectionAdapterResourceEnumeratorFactory() {
        JavaReflectionAdapterRBean rbean;
        try {
            rbean = new RBeanFactory(JavaReflectionAdapterRBean.class).createRBean(JavaReflectionAdapterRBean.class);
        } catch (RBeanFactoryException ex) {
            rbean = null;
        }
        this.rbean = rbean;
    }

    public String getDescription() {
        return "Cached JavaReflectionAdapter instances";
    }

    public boolean isAvailable() {
        return rbean != null;
    }

    public JavaReflectionAdapterResourceEnumerator createEnumerator() {
        return new JavaReflectionAdapterResourceEnumerator(resourceType, rbean.getAdapters());
    }
}
