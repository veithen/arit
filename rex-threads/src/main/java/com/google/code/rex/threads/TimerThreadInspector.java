package com.google.code.rex.threads;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.TimerTask;

public class TimerThreadInspector implements ThreadInspector {
    private final Class<?> timerThreadClass;
    private final Field queueField;
    private final Field timerTaskArrayField;
    private final Field sizeField;
    
    public TimerThreadInspector(Class<?> timerThreadClass, Field queueField, Field timerTaskArrayField, Field sizeField) {
        this.timerThreadClass = timerThreadClass;
        this.queueField = queueField;
        this.timerTaskArrayField = timerTaskArrayField;
        this.sizeField = sizeField;
    }

    public ThreadDescription getDescription(Thread thread) {
        if (timerThreadClass.isInstance(thread)) {
            try {
                StringBuilder description = new StringBuilder("Timer thread");
                Set<ClassLoader> classLoaders = new HashSet<ClassLoader>();
                Object taskQueue = queueField.get(thread);
                TimerTask[] timerTasks = (TimerTask[])timerTaskArrayField.get(taskQueue);
                int size = (Integer)sizeField.get(taskQueue);
                for (int i=1; i<=size; i++) {
                    TimerTask task = timerTasks[i];
                    Class<?> taskClass = task.getClass();
                    description.append(i == 1 ? "; tasks: " : ", ");
                    description.append(taskClass.getName());
                    classLoaders.add(taskClass.getClassLoader());
                }
                // If we have found timer tasks, then the class loaders of these tasks
                // are the most meaningful. Only add the TCCL if we have not identified
                // any tasks.
                if (classLoaders.isEmpty()) {
                    classLoaders.add(thread.getContextClassLoader());
                }
                return new ThreadDescription(description.toString(), classLoaders);
            } catch (IllegalAccessException ex) {
                throw new IllegalAccessError(ex.getMessage());
            }
        } else {
            return null;
        }
    }

    public int getPriority() {
        return 1;
    }
}
