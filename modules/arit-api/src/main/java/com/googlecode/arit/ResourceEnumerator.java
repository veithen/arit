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
package com.googlecode.arit;

public interface ResourceEnumerator {
    /**
     * Fetch the next resource from the enumerator.
     * 
     * @return <code>true</code> if a resource has been fetched; <code>false</code> if there are no
     *         more resources
     */
    boolean nextResource();
    
    /**
     * Get the type of the current resource.
     * 
     * @return the type of the current resource
     */
    ResourceType getType();
    
    /**
     * Get a (human readable) description of the current resource.
     * 
     * @return the description of the current resource
     */
    String getResourceDescription();
    
    boolean nextClassLoaderReference();
    
    ClassLoader getReferencedClassLoader();
    
    String getClassLoaderReferenceDescription();
}
