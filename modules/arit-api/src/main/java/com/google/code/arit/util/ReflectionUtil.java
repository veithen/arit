package com.google.code.arit.util;

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
}
