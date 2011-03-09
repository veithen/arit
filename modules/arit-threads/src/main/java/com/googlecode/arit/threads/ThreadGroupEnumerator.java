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
package com.googlecode.arit.threads;

import java.util.Collection;
import java.util.Collections;

import com.googlecode.arit.ResourceEnumerator;
import com.googlecode.arit.ResourceType;

public class ThreadGroupEnumerator implements ResourceEnumerator {
    private final ResourceType resourceType;
    private final ThreadGroup[] threadGroups;
    private int current = -1;
    private ThreadGroup threadGroup;

    public ThreadGroupEnumerator(ResourceType resourceType, ThreadGroup[] threadGroups) {
        this.resourceType = resourceType;
        this.threadGroups = threadGroups;
    }

    public ResourceType getType() {
        return resourceType;
    }

    public Collection<ClassLoader> getClassLoaders() {
        return Collections.singleton(threadGroup.getClass().getClassLoader());
    }

    public String getDescription() {
        return "Thread group: " + threadGroup.getName() + "; class=" + threadGroup.getClass().getName();
    }

    public boolean next() {
        if (current+1 < threadGroups.length) {
            current++;
            threadGroup = threadGroups[current];
            return true;
        } else {
            return false;
        }
    }
}
