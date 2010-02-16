package com.google.code.rex.threads;

import java.lang.reflect.Field;

import com.google.code.rex.Provider;
import com.google.code.rex.util.ReflectionUtil;

public class TimerThreadInspectorProvider implements Provider<ThreadInspector> {
    public ThreadInspector getImplementation() {
        Class<?> timerThreadClass;
        Field queueField;
        Field timerTaskArrayField;
        Field sizeField;
        try {
            timerThreadClass = Class.forName("java.util.TimerThread");
            queueField = ReflectionUtil.getField(timerThreadClass, "queue");
            Class<?> taskQueueClass = Class.forName("java.util.TaskQueue");
            timerTaskArrayField = ReflectionUtil.getField(taskQueueClass, "queue");
            sizeField = ReflectionUtil.getField(taskQueueClass, "size");
        } catch (ClassNotFoundException ex) {
            return null;
        } catch (NoSuchFieldException ex) {
            return null;
        }
        return new TimerThreadInspector(timerThreadClass, queueField, timerTaskArrayField, sizeField);
    }
}
