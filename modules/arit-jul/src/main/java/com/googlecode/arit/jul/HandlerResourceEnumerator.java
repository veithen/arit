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
package com.googlecode.arit.jul;

import java.util.logging.Handler;
import java.util.logging.LogManager;

import com.googlecode.arit.ResourceType;
import com.googlecode.arit.SimpleResourceEnumerator;

public class HandlerResourceEnumerator extends SimpleResourceEnumerator {
    private final ResourceType resourceType;
    private final HandlerEnumerator handlerEnumerator;
    
    public HandlerResourceEnumerator(ResourceType resourceType, LogManager logManager) {
        this.resourceType = resourceType;
        handlerEnumerator = new HandlerEnumerator(new LogManagerLoggerEnumerator(logManager));
    }
    
    @Override
    protected boolean doNextResource() {
        return handlerEnumerator.next();
    }

    public Handler getHandler() {
        return handlerEnumerator.getHandler();
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public Object getResourceObject() {
        return getHandler();
    }

    public String getResourceDescription() {
        return "JUL handler " + getHandler().getClass().getName() + " registered on logger " + Utils.getLoggerDisplayName(handlerEnumerator.getLogger());
    }

    public String getClassLoaderReferenceDescription() {
        return "Implementation class";
    }

    public ClassLoader getReferencedClassLoader() {
        return getHandler().getClass().getClassLoader();
    }

    public boolean cleanup() {
        handlerEnumerator.getLogger().removeHandler(getHandler());
        return true;
    }
}
