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

import java.util.Collections;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import com.googlecode.arit.ResourceType;
import com.googlecode.arit.threadutils.ThreadHelper;

/**
 * Thread inspector that retrieves the {@link Runnable} from the thread.
 * 
 * @author Andreas Veithen
 */
public class DefaultThreadInspectorPlugin implements ThreadInspectorPlugin {
    @Resource(name="thread")
    private ResourceType resourceType;

    @Autowired
    private ThreadHelper threadHelper;
    
    public boolean isAvailable() {
        return threadHelper.isAvailable();
    }

    public ThreadDescription getDescription(Thread thread) {
        Runnable target = threadHelper.getTarget(thread);
        StringBuilder description = new StringBuilder("Thread; name=");
        description.append(thread.getName());
        Class<?> threadClass = thread.getClass();
        if (threadClass != Thread.class) {
            description.append(", type=");
            description.append(threadClass.getName());
        }
        if (target != null) {
            Class<?> targetClass = target.getClass();
            description.append(", target=");
            description.append(targetClass.getName());
        }
        return new ThreadDescription(resourceType, description.toString(), Collections.<ClassLoader>emptySet());
    }

    public int getPriority() {
        return 0;
    }
}
