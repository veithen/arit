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
package com.googlecode.arit.util;

import java.lang.reflect.Field;

public class ReflectionUtil {
    private ReflectionUtil() {}
    
    public static Field getField(Class<?> clazz, String... alternatives) throws NoSuchFieldException {
        NoSuchFieldException exception = null;
        for (String name : alternatives) {
            try {
                Field field = clazz.getDeclaredField(name);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException ex) {
                if (exception == null) {
                    exception = ex;
                }
            }
        }
        throw exception;
    }
    
    public static Field getField(Class<?> clazz, Class<?> type) throws NoSuchFieldException {
        for (Field field : clazz.getDeclaredFields()) {
            if (type.isAssignableFrom(field.getType())) {
                field.setAccessible(true);
                return field;
            }
        }
        throw new NoSuchFieldException();
    }
}
