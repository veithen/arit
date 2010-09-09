package com.googlecode.arit.threads;

import com.googlecode.arit.Provider;
import com.googlecode.arit.util.ReflectionUtil;

public class DefaultThreadInspectorProvider implements Provider<ThreadInspector> {
    public ThreadInspector getImplementation() {
        try {
            // "target" is used by Sun (1.5 and 1.6)
            // "runnable" is used by IBM
            return new DefaultThreadInspector(ReflectionUtil.getField(Thread.class, "target", "runnable"));
        } catch (NoSuchFieldException ex) {
            return null;
        }
    }
}
