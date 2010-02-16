package com.google.code.rex.threads;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * Thread inspector that retrieves the {@link Runnable} from the thread.
 * 
 * @author Andreas Veithen
 */
public class DefaultThreadInspector implements ThreadInspector {
    private final Field targetField;
    
    public DefaultThreadInspector() {
        try {
            targetField = Thread.class.getDeclaredField("target");
        } catch (NoSuchFieldException ex) {
            throw new NoSuchFieldError(ex.getMessage());
        }
        targetField.setAccessible(true);
    }

    public ThreadDescription getDescription(Thread thread) {
        Runnable target;
        try {
            target = (Runnable)targetField.get(thread);
        } catch (IllegalAccessException ex) {
            throw new IllegalAccessError(ex.getMessage());
        }
        StringBuilder description = new StringBuilder("Thread");
        Set<ClassLoader> classLoaders = new HashSet<ClassLoader>();
        classLoaders.add(thread.getContextClassLoader());
        Class<?> threadClass = thread.getClass();
        if (threadClass != Thread.class) {
            description.append(", type=");
            description.append(threadClass.getName());
            classLoaders.add(threadClass.getClassLoader());
        }
        if (target != null) {
            Class<?> targetClass = target.getClass();
            description.append(", target=");
            description.append(targetClass.getName());
            classLoaders.add(targetClass.getClassLoader());
        }
        return new ThreadDescription(description.toString(), classLoaders);
    }

    public int getPriority() {
        return 0;
    }
}
