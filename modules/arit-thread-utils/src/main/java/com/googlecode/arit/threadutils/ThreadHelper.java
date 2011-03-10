/*
 * Copyright 2010-2011 Andreas Veithen
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
package com.googlecode.arit.threadutils;

import java.security.ProtectionDomain;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.codehaus.plexus.component.annotations.Component;

import com.googlecode.arit.rbeans.RBeanFactory;
import com.googlecode.arit.rbeans.RBeanFactoryException;

@Component(role=ThreadHelper.class)
public class ThreadHelper {
    private final RBeanFactory rbf;
    
    public ThreadHelper() {
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
    
    public Runnable getTarget(Thread thread) {
        return rbf.createRBean(ThreadRBean.class, thread).getTarget();
    }
    
    public Collection<ClassLoader> getReferencedClassLoaders(Thread thread) {
        ThreadRBean threadRBean = rbf.createRBean(ThreadRBean.class, thread);
        Set<ClassLoader> classLoaders = new HashSet<ClassLoader>();
        classLoaders.add(thread.getContextClassLoader());
        Class<?> threadClass = thread.getClass();
        if (threadClass != Thread.class) {
            classLoaders.add(threadClass.getClassLoader());
        }
        Runnable target = threadRBean.getTarget();
        if (target != null) {
            classLoaders.add(target.getClass().getClassLoader());
        }
        ProtectionDomain[] context = threadRBean.getAccessControlContext().getContext();
        if (context != null) {
            for (ProtectionDomain pd : context) {
                classLoaders.add(pd.getClassLoader());
            }
        }
        return classLoaders;
    }
}
