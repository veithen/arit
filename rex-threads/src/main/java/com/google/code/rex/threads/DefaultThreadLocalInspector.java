package com.google.code.rex.threads;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.Map;

public class DefaultThreadLocalInspector implements ThreadLocalInspector {
    private final Field[] threadLocalMapFields;
    private final Field tableField;
    private final Field valueField;
    
    public DefaultThreadLocalInspector(Field[] threadLocalMapFields, Field tableField, Field valueField) {
        this.threadLocalMapFields = threadLocalMapFields;
        this.tableField = tableField;
        this.valueField = valueField;
    }

    public Map<ThreadLocal<?>,Object> getThreadLocalMap(Thread thread) {
        try {
            Map<ThreadLocal<?>,Object> result = new IdentityHashMap<ThreadLocal<?>,Object>();
            for (Field threadLocalMapField : threadLocalMapFields) {
                Object threadLocalMap = threadLocalMapField.get(thread);
                if (threadLocalMap != null) {
                    Object[] table = (Object[])tableField.get(threadLocalMap);
                    for (Object entry : table) {
                        if (entry != null) {
                            ThreadLocal<?> threadLocal = (ThreadLocal<?>)((WeakReference<?>)entry).get();
                            if (threadLocal != null) {
                                result.put(threadLocal, valueField.get(entry));
                            }
                        }
                    }
                }
            }
            return result;
        } catch (IllegalAccessException ex) {
            throw new IllegalAccessError(ex.getMessage());
        }
    }
}
