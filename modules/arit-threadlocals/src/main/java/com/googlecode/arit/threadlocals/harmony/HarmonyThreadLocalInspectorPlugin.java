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
package com.googlecode.arit.threadlocals.harmony;

import java.lang.ref.WeakReference;
import java.util.IdentityHashMap;
import java.util.Map;

import org.codehaus.plexus.component.annotations.Component;

import com.googlecode.arit.rbeans.RBeanFactory;
import com.googlecode.arit.rbeans.RBeanFactoryException;
import com.googlecode.arit.threadlocals.ThreadLocalInspectorPlugin;

@Component(role=ThreadLocalInspectorPlugin.class, hint="harmony")
public class HarmonyThreadLocalInspectorPlugin implements ThreadLocalInspectorPlugin {
    private final RBeanFactory rbf;
    
    public HarmonyThreadLocalInspectorPlugin() {
        RBeanFactory rbf;
        try {
            rbf = new RBeanFactory(ThreadRBean.class);
        } catch (RBeanFactoryException ex) {
            rbf = null;
        }
        this.rbf = rbf;
    }

    public boolean isAvailable() {
        return rbf != null;
    }

    public Map<ThreadLocal<?>,Object> getThreadLocalMap(Thread thread) {
        ThreadRBean threadRBean = rbf.createRBean(ThreadRBean.class, thread);
        Map<ThreadLocal<?>,Object> result = new IdentityHashMap<ThreadLocal<?>,Object>();
        for (ValuesRBean values : new ValuesRBean[] { threadRBean.getLocalValues(), threadRBean.getInheritableValues() }) {
            if (values != null) {
                Object[] table = values.getTable();
                for (int i=0; i<table.length; i+=2) {
                    // The entry may be null, a WeakReference or a "tombstone" (which is an instance of Object)
                    Object object = table[i];
                    if (object instanceof WeakReference) {
                        WeakReference<?> reference = (WeakReference<?>)object;
                        ThreadLocal<?> threadLocal = (ThreadLocal<?>)reference.get();
                        if (threadLocal != null) {
                            result.put(threadLocal, table[i+1]);
                        }
                    }
                }
            }
        }
        return result;
    }
}
