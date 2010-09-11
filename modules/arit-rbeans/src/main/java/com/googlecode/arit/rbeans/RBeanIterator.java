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
package com.googlecode.arit.rbeans;

import java.util.Iterator;

public class RBeanIterator<T> implements Iterator<T> {
    private final RBeanFactory rbf;
    private final Class<T> rbeanClass;
    private final Iterator<?> parent;
    
    public RBeanIterator(RBeanFactory rbf, Class<T> rbeanClass, Iterator<?> parent) {
        this.rbf = rbf;
        this.rbeanClass = rbeanClass;
        this.parent = parent;
    }

    public boolean hasNext() {
        return parent.hasNext();
    }

    public T next() {
        Object value = parent.next();
        if (value == null) {
            return null;
        } else {
            return rbeanClass.cast(rbf.createRBean(rbf.getRBeanInfoForTargetClass(value.getClass()), value));
        }
    }

    public void remove() {
        parent.remove();
    }
}
