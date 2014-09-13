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

import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.Set;

import com.googlecode.arit.Formatter;
import com.googlecode.arit.resource.ClassLoaderReference;
import com.googlecode.arit.resource.SimpleClassLoaderReference;

public class ThreadUtils {
    private ThreadUtils() {}
    
    public static ThreadGroup getRootThreadGroup() {
        ThreadGroup rootThreadGroup = Thread.currentThread().getThreadGroup();
        ThreadGroup parent;
        while ((parent = rootThreadGroup.getParent()) != null) {
            rootThreadGroup = parent;
        }
        return rootThreadGroup;
    }
    
    public static Thread[] getAllThreads() {
        ThreadGroup rootThreadGroup = getRootThreadGroup();
        Thread[] threads = new Thread[64];
        int threadCount;
        while (true) {
            threadCount = rootThreadGroup.enumerate(threads);
            if (threadCount == threads.length) {
                // We probably missed threads; double the size of the array
                threads = new Thread[threads.length*2];
            } else {
                break;
            }
        }
        Thread[] result = new Thread[threadCount];
        System.arraycopy(threads, 0, result, 0, threadCount);
        return result;
    }
    
    public static ThreadGroup[] getAllThreadGroups() {
        ThreadGroup rootThreadGroup = getRootThreadGroup();
        ThreadGroup[] threadGroups = new ThreadGroup[64];
        int threadGroupCount;
        while (true) {
            threadGroupCount = rootThreadGroup.enumerate(threadGroups, true);
            if (threadGroupCount == threadGroups.length) {
                threadGroups = new ThreadGroup[threadGroups.length*2];
            } else {
                break;
            }
        }
        ThreadGroup[] result = new ThreadGroup[threadGroupCount];
        System.arraycopy(threadGroups, 0, result, 0, threadGroupCount);
        return result;
    }

	/**
	 * @return All class loaders referencing the thread directly or indirectly
	 */
	public static Set<ClassLoaderReference> getClassLoaderRefsOfThread(Thread threadObject, ThreadHelper threadHelper) {
		ThreadRBean threadRBean = threadHelper.getThreadRBean(threadObject);
		Set<ClassLoaderReference> clRefs = new HashSet<ClassLoaderReference>();

		clRefs.add(new SimpleClassLoaderReference(threadObject.getContextClassLoader(), "Context class loader"));

		Class<?> threadClass = threadObject.getClass();
		if (threadClass != Thread.class) {
			clRefs.add(new SimpleClassLoaderReference(threadClass.getClassLoader(), "Thread class: "
					+ threadObject.getClass().getName()));
		}

		Runnable target = threadRBean.getTarget();
		if (target != null) {
			clRefs.add(new SimpleClassLoaderReference(target.getClass().getClassLoader(), "Target: "
					+ threadRBean.getTarget().getClass()));
		}

		AccessControlContextRBean acc = threadRBean.getAccessControlContext();
		// On some JREs the access control context is cleared when the thread is stopped.
		// Therefore there is a slight probability that it is null.
		if (acc != null) {
			ProtectionDomain[] context = acc.getProtectionDomains();
			if (context != null) {
				for (final ProtectionDomain pd : context) {
					clRefs.add(new ClassLoaderReference() {
						public String getDescription(Formatter formatter) {
							CodeSource codeSource = pd.getCodeSource();
							return "Access control context; code base: "
									+ (codeSource == null ? "<unknown>" : formatter.formatUrl(codeSource.getLocation()));
						}

						public ClassLoader getClassLoader() {
							return pd.getClassLoader();
						}
					});
				}
			}
		}

		return clRefs;
	}
}
