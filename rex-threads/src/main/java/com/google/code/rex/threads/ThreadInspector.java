package com.google.code.rex.threads;

import java.lang.reflect.Field;

public class ThreadInspector {
    private final Field targetField;
    
    public ThreadInspector() {
        try {
            targetField = Thread.class.getDeclaredField("target");
        } catch (NoSuchFieldException ex) {
            throw new NoSuchFieldError(ex.getMessage());
        }
        targetField.setAccessible(true);
    }

    public Runnable getRunnable(Thread thread) {
        try {
            return (Runnable)targetField.get(thread);
        } catch (IllegalAccessException ex) {
            throw new IllegalAccessError(ex.getMessage());
        }
    }
}
