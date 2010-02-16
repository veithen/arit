package com.google.code.rex.threads;

import java.lang.reflect.Field;

import com.google.code.rex.Provider;

public class DefaultThreadInspectorProvider implements Provider<ThreadInspector> {
    public ThreadInspector getImplementation() {
        Field targetField;
        try {
            targetField = Thread.class.getDeclaredField("target");
        } catch (NoSuchFieldException ex) {
            return null;
        }
        targetField.setAccessible(true);
        return new DefaultThreadInspector(targetField);
    }
}
