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

import java.util.logging.LogManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.googlecode.arit.Logger;
import com.googlecode.arit.resource.ResourceEnumeratorFactory;
import com.googlecode.arit.resource.ResourceType;

public class HandlerResourceEnumeratorFactory implements ResourceEnumeratorFactory<HandlerResourceEnumerator> {
    @Autowired
    @Qualifier("jul-handler")
    private ResourceType resourceType;
    
    private final LogManager logManager;
    
    public HandlerResourceEnumeratorFactory() {
        LogManager logManager = LogManager.getLogManager();
        Class<? extends LogManager> clazz = logManager.getClass();
        try {
            // We only enable the plugin if the getLoggerNames method has not been overridden.
            // An overridden getLoggerNames method is an indication that the log manager maintains
            // multiple logger name spaces and that we need a specialized plugin. This is the
            // case for Tomcat's ClassLoaderLogManager. Note that WebSphere's WsLogManager
            // overrides getLogger, but not getLoggerNames.
            if (clazz.getMethod("getLoggerNames").getDeclaringClass().equals(LogManager.class)) {
                this.logManager = logManager;
            } else {
                this.logManager = null;
            }
        } catch (NoSuchMethodException ex) {
            throw new NoSuchMethodError(ex.getMessage());
        }
    }
    
    public String getDescription() {
        return "java.util.logging (JUL) handlers";
    }

    public boolean isAvailable() {
        return logManager != null;
    }

    public HandlerResourceEnumerator createEnumerator(Logger logger) {
        return new HandlerResourceEnumerator(resourceType, logManager);
    }
}
