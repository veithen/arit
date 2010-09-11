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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.googlecode.arit.Provider;
import com.googlecode.arit.rbeans.RBeanFactory;
import com.googlecode.arit.rbeans.RBeanFactoryException;
import com.googlecode.arit.shutdown.ShutdownHookInspector;

public class SunShutdownHookInspectorProvider implements Provider<ShutdownHookInspector> {
    public ShutdownHookInspector getImplementation() {
        try {
            RBeanFactory rbf = new RBeanFactory(ShutdownRBean.class);
            final ShutdownRBean shutdown = rbf.createRBean(ShutdownRBean.class);
            return new ShutdownHookInspector() {
                public List<Thread> getShutdownHooks() {
                    Iterable<WrappedHookRBean> wrappedHooks = shutdown.getHooks();
                    if (wrappedHooks != null) {
                        List<Thread> hooks = new ArrayList<Thread>();
                        for (WrappedHookRBean wrappedHook : wrappedHooks) {
                            hooks.add(wrappedHook.getHook());
                        }
                        return hooks;
                    } else {
                        return Collections.emptyList();
                    }
                }
            };
        } catch (RBeanFactoryException ex) {
            return null;
        }
    }
}
