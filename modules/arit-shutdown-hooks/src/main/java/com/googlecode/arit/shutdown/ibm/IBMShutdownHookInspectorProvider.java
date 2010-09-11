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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.googlecode.arit.Provider;
import com.googlecode.arit.rbeans.RBeanFactory;
import com.googlecode.arit.rbeans.RBeanFactoryException;
import com.googlecode.arit.shutdown.ShutdownHookInspector;

public class IBMShutdownHookInspectorProvider implements Provider<ShutdownHookInspector> {
    public ShutdownHookInspector getImplementation() {
        try {
            RBeanFactory rbf = new RBeanFactory(ApplicationShutdownHooksRBean.class);
            ApplicationShutdownHooksRBean rbean = rbf.createRBean(ApplicationShutdownHooksRBean.class);
            final Map<Thread,Thread> hooks = rbean.getHooks();
            return new ShutdownHookInspector() {
                public List<Thread> getShutdownHooks() {
                    return new ArrayList<Thread>(hooks.values());
                }
            };
		} catch (RBeanFactoryException ex) {
		    return null;
        }
    }
}
