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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.googlecode.arit.ResourceEnumerator;
import com.googlecode.arit.ResourceType;
import com.googlecode.arit.threadutils.ThreadHelper;

public class ShutdownHookEnumerator implements ResourceEnumerator {
    private final ResourceType resourceType;
    private final Iterator<Thread> iterator;
    private final ThreadHelper threadHelper;
    private Thread hook;
    
    public ShutdownHookEnumerator(ResourceType resourceType, List<Thread> hooks, ThreadHelper threadHelper) {
        this.resourceType = resourceType;
        iterator = hooks.iterator();
        this.threadHelper = threadHelper;
    }

    public ResourceType getType() {
        return resourceType;
    }

    public Collection<ClassLoader> getClassLoaders() {
        return threadHelper.getReferencedClassLoaders(hook);
    }

    public String getDescription() {
        return "Shutdown hook; type=" + hook.getClass().getName();
    }

    public boolean next() {
        if (iterator.hasNext()) {
            hook = iterator.next();
            return true;
        } else {
            return false;
        }
    }
}
