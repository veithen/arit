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
package com.googlecode.arit.threads;

import java.util.Collection;
import java.util.Collections;

import com.googlecode.arit.ResourceEnumerator;

public class ThreadEnumerator implements ResourceEnumerator {
    private final ThreadInspectorManager inspectorManager;
    private final Thread[] threads;
    private int current = -1;
    private Thread thread;
    private ThreadDescription description;

    public ThreadEnumerator(ThreadInspectorManager inspectorManager, Thread[] threads) {
        this.inspectorManager = inspectorManager;
        this.threads = threads;
    }

    public Collection<ClassLoader> getClassLoaders() {
        return description == null ? Collections.singleton(thread.getContextClassLoader()) : description.getClassLoaders();
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
