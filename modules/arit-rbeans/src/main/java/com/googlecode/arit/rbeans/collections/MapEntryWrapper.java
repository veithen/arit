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

import java.util.Map;

import com.googlecode.arit.rbeans.ObjectHandler;

@SuppressWarnings("unchecked")
public class MapEntryWrapper implements Map.Entry {
    private final ObjectHandler keyHandler;
    private final ObjectHandler valueHandler;
    private final Map.Entry parent;
    
    public MapEntryWrapper(ObjectHandler keyHandler, ObjectHandler valueHandler, Map.Entry parent) {
        this.keyHandler = keyHandler;
        this.valueHandler = valueHandler;
        this.parent = parent;
    }

    public Object getKey() {
        return keyHandler.handle(parent.getKey());
    }

    public Object getValue() {
        return valueHandler.handle(parent.getValue());
    }

    public Object setValue(Object value) {
        // TODO
        throw new UnsupportedOperationException();
    }
}
