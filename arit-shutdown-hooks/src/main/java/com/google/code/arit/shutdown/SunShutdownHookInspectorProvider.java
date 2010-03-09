package com.google.code.arit.shutdown;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.code.arit.Provider;
import com.google.code.arit.util.ReflectionUtil;

public class SunShutdownHookInspectorProvider implements Provider<ShutdownHookInspector> {
    public ShutdownHookInspector getImplementation() {
        try {
            Class<?> shutdownClass = Class.forName("java.lang.Shutdown");
            final Field hooksField = ReflectionUtil.getField(shutdownClass, "hooks");
            Class<?> wrapperClass = Class.forName("java.lang.Shutdown$WrappedHook");
            final Field hookField = ReflectionUtil.getField(wrapperClass, "hook");
            return new ShutdownHookInspector() {
                public List<Thread> getShutdownHooks() {
                    try {
                        Collection<?> wrappedHooks = (Collection<?>)hooksField.get(null);
                        if (wrappedHooks != null) {
                            List<Thread> hooks = new ArrayList<Thread>(wrappedHooks.size());
                            for (Object wrappedHook : wrappedHooks) {
                                hooks.add((Thread)hookField.get(wrappedHook));
                            }
                            return hooks;
                        } else {
                            return Collections.emptyList();
                        }
                    } catch (IllegalAccessException ex) {
                        throw new IllegalAccessError(ex.getMessage());
                    }
                }
            };
        } catch (ClassNotFoundException ex) {
            return null;
        } catch (NoSuchFieldException ex) {
            return null;
        }
    }
}
