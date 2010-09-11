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

public class CollectionWrapper<T> implements ObjectHandler {
    private final RBeanFactory rbf;
    private final Class<T> rbeanClass;

    public CollectionWrapper(RBeanFactory rbf, Class<T> rbeanClass) {
        this.rbf = rbf;
        this.rbeanClass = rbeanClass;
    }

    public Object handle(Object object) {
        return object == null ? null : new RBeanCollection<T>(rbf, rbeanClass, (Iterable<?>)object);
    }
}
