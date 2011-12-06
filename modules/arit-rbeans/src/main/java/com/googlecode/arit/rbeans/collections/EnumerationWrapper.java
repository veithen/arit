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
package com.googlecode.arit.rbeans.collections;

import java.util.Enumeration;

import com.googlecode.arit.rbeans.ObjectHandler;

@SuppressWarnings("unchecked")
public class EnumerationWrapper implements Enumeration {
    private final ObjectHandler objectHandler;
    private final Enumeration parent;
    
    public EnumerationWrapper(ObjectHandler objectHandler, Enumeration parent) {
        this.objectHandler = objectHandler;
        this.parent = parent;
    }

    public boolean hasMoreElements() {
        return parent.hasMoreElements();
    }

    public Object nextElement() {
        return objectHandler.handle(parent.nextElement());
    }
}
