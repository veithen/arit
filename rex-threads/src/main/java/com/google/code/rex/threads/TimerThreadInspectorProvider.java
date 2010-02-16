package com.google.code.rex.threads;

import java.lang.reflect.Field;

import com.google.code.rex.Provider;

public class TimerThreadInspectorProvider implements Provider<ThreadInspector> {
    public ThreadInspector getImplementation() {
        Class<?> timerThreadClass;
        Field queueField;
        Field timerTaskArrayField;
        Field sizeField;
        try {
            timerThreadClass = Class.forName("java.util.TimerThread");
            queueField = timerThreadClass.getDeclaredField("queue");
            queueField.setAccessible(true);
            Class<?> taskQueueClass = Class.forName("java.util.TaskQueue");
            timerTaskArrayField = taskQueueClass.getDeclaredField("queue");
            timerTaskArrayField.setAccessible(true);
            sizeField = taskQueueClass.getDeclaredField("size");
            sizeField.setAccessible(true);
        } catch (ClassNotFoundException ex) {
            return null;
        } catch (NoSuchFieldException ex) {
            return null;
        }
        return new TimerThreadInspector(timerThreadClass, queueField, timerTaskArrayField, sizeField);
    }
}
