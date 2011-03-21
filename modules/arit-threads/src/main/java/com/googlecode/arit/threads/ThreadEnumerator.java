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

import java.util.Set;

import com.googlecode.arit.ResourceType;
import com.googlecode.arit.threadutils.ThreadHelper;
import com.googlecode.arit.threadutils.ThreadObjectEnumerator;

public class ThreadEnumerator extends ThreadObjectEnumerator {
    private final ResourceType defaultResourceType;
    private final ThreadInspector inspectorManager;
    private final Thread[] threads;
    private int current = -1;
    private ThreadDescription description;

    public ThreadEnumerator(ResourceType defaultResourceType, ThreadHelper threadHelper, ThreadInspector inspectorManager, Thread[] threads) {
        super(threadHelper);
        this.defaultResourceType = defaultResourceType;
        this.inspectorManager = inspectorManager;
        this.threads = threads;
    }

    public ResourceType getResourceType() {
        return description == null ? defaultResourceType : description.getResourceType();
    }

    public String getResourceDescription() {
        return description == null ? "Thread: " + threadObject.getName() + " [" + threadObject.getId() + "] " : description.getDescription();
    }

    protected Thread nextThreadObject() {
        if (current+1 < threads.length) {
            current++;
            Thread thread = threads[current];
            description = inspectorManager.getDescription(thread);
            return thread;
        } else {
            return null;
        }
    }

    @Override
    protected Set<ClassLoader> getAdditionalClassLoaderReferences() {
        return description.getAdditionalClassLoaderReferences();
    }
}
