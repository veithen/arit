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

import java.util.Enumeration;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.googlecode.arit.ResourceType;
import com.googlecode.arit.SimpleResourceEnumerator;

public class HandlerResourceEnumerator extends SimpleResourceEnumerator {
    private final ResourceType resourceType;
    private final LogManager logManager;
    private final Enumeration<String> loggerNames;
    private Logger logger;
    private Handler[] handlers;
    private int handlerIndex;
    
    public HandlerResourceEnumerator(ResourceType resourceType, LogManager logManager) {
        this.resourceType = resourceType;
        this.logManager = logManager;
        this.loggerNames = logManager.getLoggerNames();
    }
    
    @Override
    protected boolean doNextResource() {
        while (true) {
            if (handlers == null) {
                if (loggerNames.hasMoreElements()) {
                    logger = logManager.getLogger(loggerNames.nextElement());
                    Handler[] handlers = logger.getHandlers();
                    // Logger#getHandlers() never returns null
                    if (handlers.length != 0) {
                        this.handlers = handlers;
                        handlerIndex = 0;
                        return true;
                    }
                } else {
                    handlerIndex = -1;
                    return false;
                }
            } else {
                handlerIndex++;
                if (handlerIndex == handlers.length) {
                    handlers = null;
                } else {
                    return true;
                }
            }
        }
    }

    public Handler getHandler() {
        return handlers[handlerIndex];
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public String getResourceDescription() {
        return "java.util.logging.Handler registered on logger " + logger.getName();
    }

    public String getClassLoaderReferenceDescription() {
        return "Handler implementation class";
    }

    public ClassLoader getReferencedClassLoader() {
        return getHandler().getClass().getClassLoader();
    }

    public boolean cleanup() {
        logger.removeHandler(getHandler());
        return true;
    }
}
