package com.google.code.rex.util;

import java.lang.reflect.Field;

public class ReflectionUtil {
    private ReflectionUtil() {}
    
    public static Field getField(Class<?> clazz, String name) throws NoSuchFieldException {
        Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }
}
