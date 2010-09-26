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
package com.googlecode.arit.threadlocals.sun;

import java.util.IdentityHashMap;
import java.util.Map;

import org.codehaus.plexus.component.annotations.Component;

import com.googlecode.arit.rbeans.RBeanFactory;
import com.googlecode.arit.rbeans.RBeanFactoryException;
import com.googlecode.arit.threadlocals.ThreadLocalInspectorPlugin;

@Component(role=ThreadLocalInspectorPlugin.class, hint="sun")
public class SunThreadLocalInspectorPlugin implements ThreadLocalInspectorPlugin {
    private final RBeanFactory rbf;
    
    public SunThreadLocalInspectorPlugin() {
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
        for (ThreadLocalMapRBean threadLocalMap : new ThreadLocalMapRBean[] { threadRBean.getThreadLocals(), threadRBean.getInheritableThreadLocals() }) {
            if (threadLocalMap != null) {
                for (ThreadLocalMapEntryRBean entry : threadLocalMap.getTable()) {
                    if (entry != null) {
                        ThreadLocal<?> threadLocal = (ThreadLocal<?>)entry.get();
                        if (threadLocal != null) {
                            result.put(threadLocal, entry.getValue());
                        }
                    }
                }
            }
        }
        return result;
    }
}
