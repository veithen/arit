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
package com.googlecode.arit.websphere.orb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.googlecode.arit.ResourceEnumeratorFactory;
import com.googlecode.arit.ResourceType;
import com.googlecode.arit.rbeans.RBeanFactory;
import com.googlecode.arit.rbeans.RBeanFactoryException;

public class WLMProxyEnumeratorFactory implements ResourceEnumeratorFactory<WLMProxyEnumerator> {
    private final SelectionManagerRBean rbean;
    
    @Autowired
    @Qualifier("ws-wlm-proxy")
    private ResourceType resourceType;
    
    public WLMProxyEnumeratorFactory() {
        SelectionManagerRBean rbean;
        try {
            rbean = ((WLMClientForCommonRouterImplRBean)((WLMClientRBean)new RBeanFactory(GlobalORBFactoryRBean.class).createRBean(GlobalORBFactoryRBean.class).globalORB().getWLMPlugin()).getImpl()).getSelectionManager();
        } catch (RBeanFactoryException ex) {
            rbean = null;
        }
        this.rbean = rbean;
    }

    public boolean isAvailable() {
        return rbean != null;
    }

    public String getDescription() {
        return "WLM Proxies";
    }
    
    public WLMProxyEnumerator createEnumerator() {
        return new WLMProxyEnumerator(resourceType, rbean.getProxies().values().iterator());
    }
}
