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
