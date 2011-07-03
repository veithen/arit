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

import java.util.logging.LogManager;

import javax.annotation.Resource;

import com.googlecode.arit.ResourceEnumeratorFactory;
import com.googlecode.arit.ResourceType;
import com.googlecode.arit.rbeans.RBeanFactory;
import com.googlecode.arit.rbeans.RBeanFactoryException;

public class LoggingContextResourceEnumeratorFactory implements ResourceEnumeratorFactory<LoggingContextResourceEnumerator> {
    @Resource(name="juli-context")
    private ResourceType resourceType;
    
    private final RBeanFactory rbf;
    private final ClassLoaderLogManagerRBean logManager;
    
    public LoggingContextResourceEnumeratorFactory() {
        this(LogManager.getLogManager());
    }
    
    LoggingContextResourceEnumeratorFactory(LogManager logManager) {
        RBeanFactory rbf;
        try {
            rbf = new RBeanFactory(ClassLoaderLogManagerRBean.class, ClassLoaderLogInfoRBean.class);
        } catch (RBeanFactoryException ex) {
            rbf = null;
        }
        this.rbf = rbf;
        if (rbf != null) {
            if (rbf.getRBeanInfo(ClassLoaderLogManagerRBean.class).getTargetClass().isInstance(logManager)) {
                this.logManager = rbf.createRBean(ClassLoaderLogManagerRBean.class, logManager);
            } else {
                this.logManager = null;
            }
        } else {
            this.logManager = null;
        }
    }
    
    public String getDescription() {
        return "JULI per class loader logging context";
    }

    public boolean isAvailable() {
        return logManager != null;
    }

    public LoggingContextResourceEnumerator createEnumerator() {
        return new LoggingContextResourceEnumerator(resourceType, rbf, logManager);
    }
}
