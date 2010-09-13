/*
 * Copyright 2010 Andreas Veithen
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

import java.util.List;

import javax.management.MBeanServerFactory;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;

import com.googlecode.arit.ResourceEnumerator;
import com.googlecode.arit.ResourceEnumeratorFactory;

@Component(role=ResourceEnumeratorFactory.class, hint="mbean")
public class MBeanEnumeratorFactory implements ResourceEnumeratorFactory {
    @Requirement(role=MBeanServerInspector.class)
    private List<MBeanServerInspector> mbsInspectors; 
    
    @Requirement
    private Logger logger;
    
    public boolean isAvailable() {
        for (MBeanServerInspector inspector : mbsInspectors) {
            if (inspector.isAvailable()) {
                return true;
            }
        }
        return false;
    }

    public String getDescription() {
        return "MBeans";
    }

    public ResourceEnumerator createEnumerator() {
        return new MBeanEnumerator(mbsInspectors, MBeanServerFactory.findMBeanServer(null).iterator(), logger);
    }
}
