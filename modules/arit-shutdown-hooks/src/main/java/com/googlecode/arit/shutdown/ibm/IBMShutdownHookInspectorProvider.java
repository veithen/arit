/*
 * Copyright 2010 Andreas Veithen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.arit.shutdown.ibm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.googlecode.arit.Provider;
import com.googlecode.arit.shutdown.ShutdownHookInspector;
import com.googlecode.arit.util.ReflectionUtil;

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
