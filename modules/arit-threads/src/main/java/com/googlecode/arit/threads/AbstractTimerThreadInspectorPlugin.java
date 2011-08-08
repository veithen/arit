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

import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.googlecode.arit.ResourceType;

public abstract class AbstractTimerThreadInspectorPlugin implements ThreadInspectorPlugin {
    @Autowired
    @Qualifier("timerthread")
    private ResourceType resourceType;
    
    protected abstract TimerTask[] getTimerTasks(Thread thread);
    
    public ThreadDescription getDescription(Thread thread) {
        final TimerTask[] timerTasks = getTimerTasks(thread);
        if (timerTasks != null) {
            return new ThreadDescription(resourceType, "Timer thread") {
                private int i = -1;
                
                @Override
                public boolean nextClassLoaderReference() {
                    while (i < timerTasks.length-1) {
                        if (timerTasks[++i] != null) {
                            return true;
                        }
                    }
                    return false;
                }
                
                @Override
                public ClassLoader getReferencedClassLoader() {
                    return timerTasks[i].getClass().getClassLoader();
                }
    
                @Override
                public String getClassLoaderReferenceDescription() {
                    return "Task: " + timerTasks[i].getClass().getName();
                }
            };
        } else {
            return null;
        }
    }

    public int getPriority() {
        return 1;
    }
}
