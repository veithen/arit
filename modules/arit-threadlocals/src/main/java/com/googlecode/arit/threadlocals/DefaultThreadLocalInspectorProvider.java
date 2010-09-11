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
package com.googlecode.arit.threadlocals;

import java.lang.reflect.Field;

import com.googlecode.arit.Provider;
import com.googlecode.arit.util.ReflectionUtil;

public class DefaultThreadLocalInspectorProvider implements Provider<ThreadLocalInspector> {
    public ThreadLocalInspector getImplementation() {
        try {
            Field[] threadLocalMapFields = new Field[] {
                    ReflectionUtil.getField(Thread.class, "threadLocals"),
                    ReflectionUtil.getField(Thread.class, "inheritableThreadLocals")
            };
            Class<?> threadLocalMapClass = Class.forName("java.lang.ThreadLocal$ThreadLocalMap");
            Field tableField = ReflectionUtil.getField(threadLocalMapClass, "table");
            Class<?> entryClass = Class.forName("java.lang.ThreadLocal$ThreadLocalMap$Entry");
            Field valueField = ReflectionUtil.getField(entryClass, "value");
            return new DefaultThreadLocalInspector(threadLocalMapFields, tableField, valueField);
        } catch (NoSuchFieldException ex) {
            return null;
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }
}
