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

import com.googlecode.arit.Formatter;
import com.googlecode.arit.ResourceType;
import com.googlecode.arit.threadutils.ThreadHelper;
import com.googlecode.arit.threadutils.ThreadObjectEnumerator;

public class ThreadEnumerator extends ThreadObjectEnumerator {
    private final ThreadInspector inspectorManager;
    private final Thread[] threads;
    private int current = -1;
    private ThreadDescription description;

    public ThreadEnumerator(ThreadHelper threadHelper, ThreadInspector inspectorManager, Thread[] threads) {
        super(threadHelper);
        this.inspectorManager = inspectorManager;
        this.threads = threads;
    }

    public ResourceType getResourceType() {
        return description.getResourceType();
    }

    public String getResourceDescription(Formatter formatter) {
        return description.getDescription() + ": " + threadObject.getName() + " [" + threadObject.getId() + "]";
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
    protected boolean nextOtherClassLoaderReference() {
        return description.nextClassLoaderReference();
    }

    @Override
    protected ClassLoader getOtherReferencedClassLoader() {
        return description.getReferencedClassLoader();
    }

    @Override
    protected String getOtherClassLoaderReferenceDescription() {
        return description.getClassLoaderReferenceDescription();
    }

    public boolean cleanup() {
        return false;
    }
}
