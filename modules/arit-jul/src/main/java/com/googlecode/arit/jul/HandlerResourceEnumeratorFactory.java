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

import javax.annotation.Resource;

import com.googlecode.arit.ResourceEnumeratorFactory;
import com.googlecode.arit.ResourceType;

public class HandlerResourceEnumeratorFactory implements ResourceEnumeratorFactory<HandlerResourceEnumerator> {
    @Resource(name="jul-handler")
    private ResourceType resourceType;
    
    private final LogManager logManager;
    
    public HandlerResourceEnumeratorFactory() {
        LogManager logManager = LogManager.getLogManager();
        Class<? extends LogManager> clazz = logManager.getClass();
        try {
            // We only enable the plugin if the getLogger and getLoggerNames methods have not
            // been overridden. For LogManager implementations that override these methods
            // (such as Tomcat's ClassLoaderLogManager), we need specialized plugins. 
            if (clazz.getMethod("getLogger", String.class).getDeclaringClass().equals(LogManager.class)
                    && clazz.getMethod("getLoggerNames").getDeclaringClass().equals(LogManager.class)) {
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

    public HandlerResourceEnumerator createEnumerator() {
        return new HandlerResourceEnumerator(resourceType, logManager);
    }
}
