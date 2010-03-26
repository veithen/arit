package com.google.code.arit.shutdown;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.code.arit.Provider;
import com.google.code.arit.util.ReflectionUtil;

public class IBMShutdownHookInspectorProvider implements Provider<ShutdownHookInspector> {
    public ShutdownHookInspector getImplementation() {
        try {
            Class<?> shutdownHooksClass = Class.forName("java.lang.ApplicationShutdownHooks");
            Field hooksField = ReflectionUtil.getField(shutdownHooksClass, "hooks");
            final Map<?,?> hooks = (Map<?,?>)hooksField.get(null);
            return new ShutdownHookInspector() {
                public List<Thread> getShutdownHooks() {
                    List<Thread> result = new ArrayList<Thread>(hooks.size());
                    for (Object hook : hooks.values()) {
                        result.add((Thread)hook);
                    }
                    return result;
                }
            };
        } catch (ClassNotFoundException ex) {
            return null;
        } catch (NoSuchFieldException ex) {
            return null;
		} catch (IllegalAccessException ex) {
			return null;
		}
    }
}
