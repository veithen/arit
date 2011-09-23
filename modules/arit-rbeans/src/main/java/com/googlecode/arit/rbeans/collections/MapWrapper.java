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

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.googlecode.arit.rbeans.ObjectHandler;

@SuppressWarnings("unchecked")
public class MapWrapper implements Map {
    private final ObjectHandler keyHandler;
    private final ObjectHandler valueHandler;
    private final Map parent;
    
    public MapWrapper(ObjectHandler keyHandler, ObjectHandler valueHandler, Map parent) {
        this.keyHandler = keyHandler;
        this.valueHandler = valueHandler;
        this.parent = parent;
    }

    public ObjectHandler getKeyHandler() {
        return keyHandler;
    }

    public ObjectHandler getValueHandler() {
        return valueHandler;
    }

    public int size() {
        return parent.size();
    }

    public Set entrySet() {
        return new SetWrapper(new MapEntryHandler(keyHandler, valueHandler), parent.entrySet());
    }

    public Set keySet() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public Collection values() {
        return new CollectionWrapper(valueHandler, parent.values());
    }

    public void clear() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public boolean containsKey(Object key) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public boolean containsValue(Object value) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public Object get(Object key) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public boolean isEmpty() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public Object put(Object key, Object value) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void putAll(Map t) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public Object remove(Object key) {
        // TODO
        throw new UnsupportedOperationException();
    }
}
