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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.googlecode.arit.ResourceEnumerator;

public abstract class ThreadObjectEnumerator implements ResourceEnumerator {
    private static final int REF_CCL = 1;
    private static final int REF_THREAD_CLASS = 2;
    private static final int REF_TARGET = 3;
    private static final int REF_ACC = 4;
    private static final int REF_OTHER = 5;
    
    protected final ThreadHelper threadHelper;
    protected Thread threadObject;
    protected ThreadRBean threadRBean;
    private int clRef;
    private Iterator<ClassLoader> classLoaderIterator;
    private ClassLoader classLoader;
    
    public ThreadObjectEnumerator(ThreadHelper threadHelper) {
        this.threadHelper = threadHelper;
    }
    
    public final boolean nextClassLoaderReference() {
        while (true) {
            if (classLoaderIterator != null) {
                if (classLoaderIterator.hasNext()) {
                    classLoader = classLoaderIterator.next();
                    return true;
                } else {
                    classLoaderIterator = null;
                }
            }
            clRef++;
            switch (clRef) {
                case REF_CCL:
                    classLoader = threadObject.getContextClassLoader();
                    return true;
                case REF_THREAD_CLASS:
                    Class<?> threadClass = threadObject.getClass();
                    if (threadClass != Thread.class) {
                        classLoader = threadClass.getClassLoader();
                        return true;
                    }
                    break;
                case REF_TARGET:
                    Runnable target = threadRBean.getTarget();
                    if (target != null) {
                        classLoader = target.getClass().getClassLoader();
                        return true;
                    }
                    break;
                case REF_ACC:
                    ProtectionDomain[] context = threadRBean.getAccessControlContext().getProtectionDomains();
                    Set<ClassLoader> classLoaders = new HashSet<ClassLoader>();
                    if (context != null) {
                        for (ProtectionDomain pd : context) {
                            classLoaders.add(pd.getClassLoader());
                        }
                    }
                    classLoaderIterator = classLoaders.iterator();
                    break;
                case REF_OTHER:
                    classLoaderIterator = getAdditionalClassLoaderReferences().iterator();
                    break;
                default:
                    return false;
            }
        }
    }

    public final ClassLoader getReferencedClassLoader() {
        return classLoader;
    }
    
    public final String getClassLoaderReferenceDescription() {
        switch (clRef) {
            case REF_CCL: return "Context class loader";
            case REF_THREAD_CLASS: return "Thread class: " + threadObject.getClass().getName();
            case REF_TARGET: return "Target: " + threadRBean.getTarget().getClass();
            case REF_ACC: return "Access control context";
            case REF_OTHER: return "Other"; // TODO: we need to get a meaningful description somehow
            default: return null;
        }
    }

    protected abstract Set<ClassLoader> getAdditionalClassLoaderReferences();
    
    public final boolean nextResource() {
        clRef = 0;
        classLoaderIterator = null;
        threadObject = nextThreadObject();
        if (threadObject == null) {
            return false;
        } else {
            threadRBean = threadHelper.getThreadRBean(threadObject);
            return true;
        }
    }

    protected abstract Thread nextThreadObject();
}
