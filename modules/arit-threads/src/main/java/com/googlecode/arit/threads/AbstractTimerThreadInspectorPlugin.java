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
package com.googlecode.arit.threads;

import java.util.HashSet;
import java.util.Set;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.googlecode.arit.resource.ClassLoaderReference;
import com.googlecode.arit.resource.ResourceType;
import com.googlecode.arit.resource.SimpleClassLoaderReference;

public abstract class AbstractTimerThreadInspectorPlugin implements ThreadInspectorPlugin {
    @Autowired
    @Qualifier("timerthread")
    private ResourceType resourceType;
    
    protected abstract TimerTask[] getTimerTasks(Thread thread);
    
	public ThreadResource getThreadResource(Thread thread) {
        final TimerTask[] timerTasks = getTimerTasks(thread);
        if (timerTasks != null) {
			ThreadResource threadResource = new ThreadResource(thread, resourceType, "Timer thread");
			Set<ClassLoaderReference> classLoaders = new HashSet<ClassLoaderReference>();
			for (final TimerTask timerTask : timerTasks) {
				// the timerTasks array can contain many null values
				if (timerTask != null) {
					classLoaders.add(new SimpleClassLoaderReference(timerTask.getClass().getClassLoader(), "Task: "
							+ timerTask.getClass().getName()));
				}
			}
			threadResource.addReferencedClassLoaders(classLoaders);
			return threadResource;
        } else {
            return null;
        }
    }

    public int getPriority() {
        return 1;
    }
}
