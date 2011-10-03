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
package com.googlecode.arit.websphere;

import java.lang.ref.WeakReference;

public class ClassLoaderInfo {
    private final WeakReference<ClassLoader> ref;
    private final String name;
    private boolean stopped;

    public ClassLoaderInfo(ClassLoader classLoader, String name) {
        ref = new WeakReference<ClassLoader>(classLoader);
        this.name = name;
    }
    
    public ClassLoader getClassLoader() {
        return ref.get();
    }

    public String getName() {
        return name;
    }

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    @Override
    public String toString() {
        return "ClassLoaderInfo[name=" + name + ",stopped=" + stopped + ",destroyed=" + (ref.get() == null) + "]";
    }
}
