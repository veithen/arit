package com.google.code.rex.threads;

import java.lang.reflect.Field;

import com.google.code.rex.Provider;

public class DefaultThreadLocalInspectorProvider implements Provider<ThreadLocalInspector> {
    public ThreadLocalInspector getImplementation() {
        try {
            Field[] threadLocalMapFields = new Field[] {
                    Thread.class.getDeclaredField("threadLocals"),
                    Thread.class.getDeclaredField("inheritableThreadLocals")
            };
            for (Field field : threadLocalMapFields) {
                field.setAccessible(true);
            }
            Class<?> threadLocalMapClass = Class.forName("java.lang.ThreadLocal$ThreadLocalMap");
            Field tableField = threadLocalMapClass.getDeclaredField("table");
            tableField.setAccessible(true);
            Class<?> entryClass = Class.forName("java.lang.ThreadLocal$ThreadLocalMap$Entry");
            Field valueField = entryClass.getDeclaredField("value");
            valueField.setAccessible(true);
            return new DefaultThreadLocalInspector(threadLocalMapFields, tableField, valueField);
        } catch (NoSuchFieldException ex) {
            return null;
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }
}
