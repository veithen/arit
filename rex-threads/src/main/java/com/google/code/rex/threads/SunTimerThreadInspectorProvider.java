package com.google.code.rex.threads;

import com.google.code.rex.Provider;
import com.google.code.rex.util.ReflectionUtil;

public class SunTimerThreadInspectorProvider implements Provider<ThreadInspector> {
    public ThreadInspector getImplementation() {
        try {
            Class<?> timerThreadClass = Class.forName("java.util.TimerThread");
            Class<?> taskQueueClass = Class.forName("java.util.TaskQueue");
            return new TimerThreadInspector(timerThreadClass,
                    ReflectionUtil.getField(timerThreadClass, "queue"),
                    ReflectionUtil.getField(taskQueueClass, "queue"));
        } catch (ClassNotFoundException ex) {
            return null;
        } catch (NoSuchFieldException ex) {
            return null;
        }
    }
}
