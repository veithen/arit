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
package com.googlecode.arit.shutdown;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

import com.googlecode.arit.ResourceEnumerator;
import com.googlecode.arit.ResourceEnumeratorFactory;
import com.googlecode.arit.ResourceType;
import com.googlecode.arit.threadutils.ThreadHelper;

@Component(role=ResourceEnumeratorFactory.class, hint="shutdown")
public class ShutdownHookEnumeratorFactory implements ResourceEnumeratorFactory {
    @Requirement(hint="shutdown")
    private ResourceType resourceType;

    @Requirement
    private ShutdownHookInspector inspector;

    @Requirement
    private ThreadHelper threadHelper;
    
    public boolean isAvailable() {
        return inspector.isAvailable() && threadHelper.isAvailable();
    }

    public String getDescription() {
        return "Shutdown hooks";
    }

    public ResourceEnumerator createEnumerator() {
        return new ShutdownHookEnumerator(resourceType, inspector.getShutdownHooks(), threadHelper);
    }
}
