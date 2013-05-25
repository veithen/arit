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
package com.googlecode.arit.discovery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.github.veithen.rbeans.RBeanFactory;
import com.github.veithen.rbeans.RBeanFactoryException;
import com.googlecode.arit.CleanerPlugin;
import com.googlecode.arit.Logger;
import com.googlecode.arit.ResourceEnumeratorFactory;
import com.googlecode.arit.ResourceType;

public class EnvironmentCacheEnumeratorFactory implements ResourceEnumeratorFactory<EnvironmentCacheEnumerator>, CleanerPlugin {
    private static final Log log = LogFactory.getLog(EnvironmentCacheEnumeratorFactory.class);
    
    private final EnvironmentCacheRBean rbean;
    
    @Autowired
    @Qualifier("env-cache-entry")
    private ResourceType resourceType;
    
    public EnvironmentCacheEnumeratorFactory() {
        EnvironmentCacheRBean rbean;
        try {
            rbean = new RBeanFactory(EnvironmentCacheRBean.class).createRBean(EnvironmentCacheRBean.class);
        } catch (RBeanFactoryException ex) {
            rbean = null;
        }
        this.rbean = rbean;
    }

    public String getDescription() {
        return "EnvironmentCache entries";
    }

    public boolean isAvailable() {
        return rbean != null;
    }

    public EnvironmentCacheEnumerator createEnumerator(Logger logger) {
        return new EnvironmentCacheEnumerator(resourceType, rbean.getRootCache());
    }

    public void clean(ClassLoader classLoader) {
        boolean removed;
        synchronized (rbean._getTargetClass()) {
            removed = rbean.getRootCache().remove(classLoader) != null;
        }
        if (removed) {
            log.info("Cleaned up EnvironmentCache entries");
        }
    }
}
