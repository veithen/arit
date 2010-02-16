package com.google.code.rex.threads;

import java.lang.reflect.Field;

import com.google.code.rex.Provider;
import com.google.code.rex.util.ReflectionUtil;

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
