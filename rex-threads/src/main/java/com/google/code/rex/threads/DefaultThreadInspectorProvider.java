package com.google.code.rex.threads;

import com.google.code.rex.Provider;
import com.google.code.rex.util.ReflectionUtil;

public class DefaultThreadInspectorProvider implements Provider<ThreadInspector> {
    public ThreadInspector getImplementation() {
        try {
            return new DefaultThreadInspector(ReflectionUtil.getField(Thread.class, "target"));
        } catch (NoSuchFieldException ex) {
            return null;
        }
    }
}
