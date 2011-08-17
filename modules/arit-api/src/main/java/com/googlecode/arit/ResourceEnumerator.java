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
    ResourceType getResourceType();
    
    /**
     * Get the object that represents this resource. The returned object may be of any type. The
     * only constraint is that the same instance must be returned when a resources is visited twice.
     * 
     * @return the object representing this resource; must not be <code>null</code>
     */
    Object getResourceObject();
    
    /**
     * Get a (human readable) description of the current resource.
     * @param formatter TODO
     * 
     * @return the description of the current resource
     */
    String getResourceDescription(Formatter formatter);
    
    boolean nextClassLoaderReference();
    
    ClassLoader getReferencedClassLoader();
    
    String getClassLoaderReferenceDescription(Formatter formatter);
    
    /**
     * Clean up this resource, i.e. attempt to break the link between the resource and the
     * referenced class loader (returned by {@link #getReferencedClassLoader()}.
     * 
     * @return <code>true</code> if the resource has been cleaned up, <code>false</code> if this
     *         operation is not supported or if the cleanup is not possible for some other reasons
     */
    boolean cleanup();
}
