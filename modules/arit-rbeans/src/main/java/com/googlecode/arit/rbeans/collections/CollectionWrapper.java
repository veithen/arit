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

import com.googlecode.arit.rbeans.ObjectHandler;

@SuppressWarnings("unchecked")
public class CollectionWrapper extends IterableWrapper implements Collection {

    public CollectionWrapper(ObjectHandler objectHandler, Collection parent) {
        super(objectHandler, parent);
    }

    public boolean add(Object o) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public boolean addAll(Collection c) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void clear() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public boolean contains(Object o) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public boolean containsAll(Collection c) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public boolean isEmpty() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public boolean remove(Object o) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public boolean removeAll(Collection c) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public boolean retainAll(Collection c) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public int size() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public Object[] toArray() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public Object[] toArray(Object[] a) {
        // TODO
        throw new UnsupportedOperationException();
    }
}
