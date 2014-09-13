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
package com.googlecode.arit.mbeans;

import javax.management.MBeanServerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.googlecode.arit.Logger;
import com.googlecode.arit.resource.ResourceEnumeratorFactory;
import com.googlecode.arit.resource.ResourceType;

public class MBeanEnumeratorFactory implements ResourceEnumeratorFactory<MBeanEnumerator> {
    @Autowired
    @Qualifier("mbean")
    private ResourceType resourceType;

    @Autowired
    private MBeanServerInspector mbsInspector; 
    
    // TODO
//    @Requirement
//    private Logger logger;
    
    public boolean isAvailable() {
        return mbsInspector.isAvailable();
    }

    public String getDescription() {
        return "MBeans";
    }

    public MBeanEnumerator createEnumerator(Logger logger) {
        return new MBeanEnumerator(resourceType, mbsInspector, MBeanServerFactory.findMBeanServer(null).iterator()/*, logger*/);
    }
}
