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

public class TieToStubInfoCacheEnumeratorFactory implements ResourceEnumeratorFactory<TieToStubInfoCacheEnumerator> {
    private final RBeanFactory rbf;
    private final TieToStubInfoCacheRBean rbean;
    
    @Autowired
    @Qualifier("websphere-bug")
    private ResourceType resourceType;
    
    public TieToStubInfoCacheEnumeratorFactory() {
        RBeanFactory rbf;
        try {
            rbf = new RBeanFactory(TieToStubInfoCacheRBean.class, DataValueListEntryRBean.class, StubInfoRBean.class);
        } catch (RBeanFactoryException ex) {
            rbf = null;
        }
        this.rbf = rbf;
        this.rbean = rbf == null ? null : rbf.createRBean(TieToStubInfoCacheRBean.class);
    }

    public String getDescription() {
        return "Tie to stub info cache entry";
    }

    public boolean isAvailable() {
        return rbean != null;
    }

    public TieToStubInfoCacheEnumerator createEnumerator() {
        return new TieToStubInfoCacheEnumerator(resourceType, rbf, rbean.getMap().getMaps());
    }
}
