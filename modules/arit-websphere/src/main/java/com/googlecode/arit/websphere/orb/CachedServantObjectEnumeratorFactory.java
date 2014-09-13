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

import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.github.veithen.rbeans.RBeanFactory;
import com.github.veithen.rbeans.RBeanFactoryException;
import com.googlecode.arit.Logger;
import com.googlecode.arit.resource.CleanerPlugin;
import com.googlecode.arit.resource.ResourceEnumeratorFactory;
import com.googlecode.arit.resource.ResourceType;

public class CachedServantObjectEnumeratorFactory implements ResourceEnumeratorFactory<CachedServantObjectEnumerator>, CleanerPlugin {
    private static final Log log = LogFactory.getLog(CachedServantObjectEnumeratorFactory.class);
    
    private final EJSRootOAImplRBean rbean;
    
    @Autowired
    @Qualifier("cached-servant-object")
    private ResourceType resourceType;
    
    public CachedServantObjectEnumeratorFactory() {
        ObjectResolverRBean rbean;
        try {
            rbean = new RBeanFactory(GlobalORBFactoryRBean.class).createRBean(GlobalORBFactoryRBean.class).globalORB().getObjectResolver();
        } catch (RBeanFactoryException ex) {
            rbean = null;
        }
        this.rbean = rbean instanceof EJSRootOAImplRBean ? (EJSRootOAImplRBean)rbean : null;
    }

    public boolean isAvailable() {
        return rbean != null;
    }

    public String getDescription() {
        return "Cached servant objects";
    }

    public CachedServantObjectEnumerator createEnumerator(Logger logger) {
        return new CachedServantObjectEnumerator(resourceType, rbean.getServantObjects().elements());
    }

    public void clean(ClassLoader classLoader) {
        Enumeration<ObjectImplRBean> e = rbean.getServantObjects().elements();
        while (e.hasMoreElements()) {
            ObjectImplRBean objectImpl = e.nextElement();
            DelegateRBean delegate = objectImpl.getDelegate();
            if (delegate instanceof ClientDelegateRBean) {
                Object servant = ((ClientDelegateRBean)delegate).getIOR().getServant();
                if (servant != null && servant.getClass().getClassLoader() == classLoader) {
                    rbean.unregisterServant((org.omg.CORBA.Object)objectImpl._getTargetObject());
                    log.info("Removed cached servant object " + servant.getClass().getName());
                }
            }
        }
    }
}
