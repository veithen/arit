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
package com.googlecode.arit.jcl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.googlecode.arit.Messages;
import com.googlecode.arit.resource.ResourceEnumeratorFactory;
import com.googlecode.arit.resource.ResourceType;

public class LogFactoryResourceEnumeratorFactory implements ResourceEnumeratorFactory<LogFactoryResourceEnumerator> {
    @Autowired
    private LogFactoryLoader logFactoryLoader;
    
    @Autowired
    @Qualifier("jcl-factory")
    private ResourceType resourceType;
    
    public String getDescription() {
        return "Cached JCL LogFactory instances";
    }

    public boolean isAvailable() {
        return logFactoryLoader.isAvailable();
    }

    public LogFactoryResourceEnumerator createEnumerator(Messages logger) {
        return new LogFactoryResourceEnumerator(resourceType, logFactoryLoader.getLogFactories());
    }
}
