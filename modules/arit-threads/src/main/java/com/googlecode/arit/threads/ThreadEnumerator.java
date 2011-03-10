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
import java.util.HashSet;
import java.util.Set;

import com.googlecode.arit.ResourceEnumerator;
import com.googlecode.arit.ResourceType;
import com.googlecode.arit.threadutils.ThreadHelper;

public class ThreadEnumerator implements ResourceEnumerator {
    private final ResourceType defaultResourceType;
    private final ThreadHelper threadHelper;
    private final ThreadInspector inspectorManager;
    private final Thread[] threads;
    private int current = -1;
    private Thread thread;
    private ThreadDescription description;

    public ThreadEnumerator(ResourceType defaultResourceType, ThreadHelper threadHelper, ThreadInspector inspectorManager, Thread[] threads) {
        this.defaultResourceType = defaultResourceType;
        this.threadHelper = threadHelper;
        this.inspectorManager = inspectorManager;
        this.threads = threads;
    }

    public ResourceType getType() {
        return description == null ? defaultResourceType : description.getResourceType();
    }

    public Collection<ClassLoader> getClassLoaders() {
        Set<ClassLoader> classLoaders = new HashSet<ClassLoader>();
        classLoaders.addAll(threadHelper.getReferencedClassLoaders(thread));
        if (description != null) {
            classLoaders.addAll(description.getAdditionalClassLoaderReferences());
        }
        return classLoaders;
    }

    public String getDescription() {
        return description == null ? "Thread: " + thread.getName() + " [" + thread.getId() + "] " : description.getDescription();
    }

    public boolean next() {
        if (current+1 < threads.length) {
            current++;
            thread = threads[current];
            description = inspectorManager.getDescription(thread);
            return true;
        } else {
            return false;
        }
    }
}
