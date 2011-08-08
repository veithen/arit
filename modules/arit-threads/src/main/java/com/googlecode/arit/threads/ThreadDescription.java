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

import com.googlecode.arit.ResourceType;

/**
 * Provides a description of a given thread instance. Implementations of this interface are used to
 * identify the resource type and to describe the thread. They can also provide additional class
 * loader references not identified by {@link ThreadEnumerator}.
 */
public abstract class ThreadDescription {
    private final ResourceType resourceType;
    private final String description;
    
    public ThreadDescription(ResourceType resourceType, String description) {
        this.resourceType = resourceType;
        this.description = description;
    }

    public final ResourceType getResourceType() {
        return resourceType;
    }

    public final String getDescription() {
        return description;
    }

    public abstract boolean nextClassLoaderReference();
    
    public abstract ClassLoader getReferencedClassLoader();
    
    public abstract String getClassLoaderReferenceDescription();
}
