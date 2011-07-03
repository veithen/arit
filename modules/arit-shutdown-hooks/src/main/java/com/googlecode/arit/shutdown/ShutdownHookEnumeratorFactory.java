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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.googlecode.arit.ResourceEnumeratorFactory;
import com.googlecode.arit.ResourceType;
import com.googlecode.arit.threadutils.ThreadHelper;

public class ShutdownHookEnumeratorFactory implements ResourceEnumeratorFactory<ShutdownHookEnumerator> {
    @Autowired
    @Qualifier("shutdown-hook")
    private ResourceType resourceType;

    @Autowired
    private ShutdownHookInspector inspector;

    @Autowired
    private ThreadHelper threadHelper;
    
    public boolean isAvailable() {
        return inspector.isAvailable() && threadHelper.isAvailable();
    }

    public String getDescription() {
        return "Shutdown hooks";
    }

    public ShutdownHookEnumerator createEnumerator() {
        return new ShutdownHookEnumerator(resourceType, inspector.getShutdownHooks(), threadHelper);
    }
}
