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

import java.util.Arrays;

public class CollectionWrapper<T> implements ObjectHandler {
    private final RBeanFactory rbf;
    private final Class<T> rbeanClass;
    private final boolean fromArray;

    public CollectionWrapper(RBeanFactory rbf, Class<T> rbeanClass, boolean fromArray) {
        this.rbf = rbf;
        this.rbeanClass = rbeanClass;
        this.fromArray = fromArray;
    }

    public Object handle(Object object) {
        if (object == null) {
            return null;
        } else {
            return new RBeanCollection<T>(rbf, rbeanClass, fromArray ? Arrays.asList((Object[])object) : (Iterable<?>)object);
        }
    }
}
