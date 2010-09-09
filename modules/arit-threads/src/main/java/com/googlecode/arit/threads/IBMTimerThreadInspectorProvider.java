package com.googlecode.arit.threads;

import com.googlecode.arit.Provider;
import com.googlecode.arit.util.ReflectionUtil;

public class IBMTimerThreadInspectorProvider implements Provider<ThreadInspector> {
    public ThreadInspector getImplementation() {
        try {
            Class<?> timerThreadClass = Class.forName("java.util.Timer$TimerImpl");
            Class<?> taskQueueClass = Class.forName("java.util.Timer$TimerImpl$TimerHeap");
            return new TimerThreadInspector(timerThreadClass,
                    ReflectionUtil.getField(timerThreadClass, "tasks"),
                    ReflectionUtil.getField(taskQueueClass, "timers"));
        } catch (ClassNotFoundException ex) {
            return null;
        } catch (NoSuchFieldException ex) {
            return null;
        }
    }
}
