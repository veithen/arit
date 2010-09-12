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
package com.googlecode.arit.threads;

import java.util.HashSet;
import java.util.Set;

import org.codehaus.plexus.component.annotations.Component;

import com.googlecode.arit.rbeans.RBeanFactory;
import com.googlecode.arit.rbeans.RBeanFactoryException;

/**
 * Thread inspector that retrieves the {@link Runnable} from the thread.
 * 
 * @author Andreas Veithen
 */
@Component(role=ThreadInspector.class)
public class DefaultThreadInspector implements ThreadInspector {
    private final RBeanFactory rbf;
    
    public DefaultThreadInspector() {
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

    public ThreadDescription getDescription(Thread thread) {
        Runnable target = rbf.createRBean(ThreadRBean.class, thread).getTarget();
        StringBuilder description = new StringBuilder("Thread");
        Set<ClassLoader> classLoaders = new HashSet<ClassLoader>();
        classLoaders.add(thread.getContextClassLoader());
        Class<?> threadClass = thread.getClass();
        if (threadClass != Thread.class) {
            description.append(", type=");
            description.append(threadClass.getName());
            classLoaders.add(threadClass.getClassLoader());
        }
        if (target != null) {
            Class<?> targetClass = target.getClass();
            description.append(", target=");
            description.append(targetClass.getName());
            classLoaders.add(targetClass.getClassLoader());
        }
        return new ThreadDescription(description.toString(), classLoaders);
    }

    public int getPriority() {
        return 0;
    }
}
