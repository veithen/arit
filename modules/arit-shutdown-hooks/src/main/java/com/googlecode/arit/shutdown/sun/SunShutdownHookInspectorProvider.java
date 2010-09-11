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
package com.googlecode.arit.shutdown.sun;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.googlecode.arit.Provider;
import com.googlecode.arit.rbeans.RBeanFactory;
import com.googlecode.arit.rbeans.RBeanFactoryException;
import com.googlecode.arit.shutdown.ShutdownHookInspector;
import com.googlecode.arit.util.ReflectionUtil;

public class SunShutdownHookInspectorProvider implements Provider<ShutdownHookInspector> {
    public ShutdownHookInspector getImplementation() {
        try {
            RBeanFactory rbf = new RBeanFactory(ShutdownRBean.class);
            final ShutdownRBean shutdown = rbf.createRBean(ShutdownRBean.class);
            Class<?> wrapperClass = Class.forName("java.lang.Shutdown$WrappedHook");
            final Field hookField = ReflectionUtil.getField(wrapperClass, "hook");
            return new ShutdownHookInspector() {
                public List<Thread> getShutdownHooks() {
                    try {
                        Collection<?> wrappedHooks = shutdown.getHooks();
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
        } catch (RBeanFactoryException ex) {
            return null;
        }
    }
}
