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
package com.googlecode.arit.jul.juli;

import java.util.Iterator;
import java.util.Map;

import com.github.veithen.rbeans.RBeanFactory;
import com.googlecode.arit.Formatter;
import com.googlecode.arit.jul.HandlerEnumerator;
import com.googlecode.arit.jul.Utils;
import com.googlecode.arit.resource.ResourceEnumerator;
import com.googlecode.arit.resource.ResourceType;

public class LoggingContextResourceEnumerator implements ResourceEnumerator {
    private final ResourceType resourceType;
    private final RBeanFactory rbf;
    private final Iterator<Map.Entry<ClassLoader,Object>> contextIterator;
    private ClassLoader classLoader;
    private Object object;
    private ClassLoaderLogInfoRBean logInfo;
    private int state;
    private HandlerEnumerator handlerEnumerator;
    
    public LoggingContextResourceEnumerator(ResourceType resourceType, RBeanFactory rbf, ClassLoaderLogManagerRBean logManager) {
        this.resourceType = resourceType;
        this.rbf = rbf;
        contextIterator = logManager.getClassLoaderLogInfoMap().entrySet().iterator();
    }
    
    public boolean nextResource() {
        if (contextIterator.hasNext()) {
            Map.Entry<ClassLoader,?> entry = contextIterator.next();
            classLoader = entry.getKey();
            object = entry.getValue();
            logInfo = rbf.createRBean(ClassLoaderLogInfoRBean.class, object);
            state = 0;
            return true;
        } else {
            return false;
        }
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public Object getResourceObject() {
        return object;
    }

    public String getResourceDescription(Formatter formatter) {
        return "JULI per class loader logging context";
    }

    public boolean nextClassLoaderReference() {
        if (state == 0) {
            state = 1;
            return true;
        } else {
            if (state == 1) {
                state = 2;
                handlerEnumerator = new HandlerEnumerator(new ContextLoggerEnumerator(logInfo));
            }
            return handlerEnumerator.next();
        }
    }

    public ClassLoader getReferencedClassLoader() {
        switch (state) {
            case 1:
                return classLoader;
            case 2:
                return handlerEnumerator.getHandler().getClass().getClassLoader();
            default:
                throw new IllegalStateException();
        }
    }

    public String getClassLoaderReferenceDescription(Formatter formatter) {
        switch (state) {
            case 1:
                return "Context selector";
            case 2:
                return "Handler " + handlerEnumerator.getHandler().getClass().getName() + " registered on logger " + Utils.getLoggerDisplayName(handlerEnumerator.getLogger());
            default:
                throw new IllegalStateException();
        }
    }

    public boolean cleanup() {
        // TODO
        return false;
    }
}
