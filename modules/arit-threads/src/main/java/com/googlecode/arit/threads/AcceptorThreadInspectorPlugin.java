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

import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Set;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

import com.googlecode.arit.ResourceType;
import com.googlecode.arit.threadutils.ThreadHelper;

/**
 * Plugin that attempts to identify TCP acceptor threads. It is based on the idea that in most
 * cases, an acceptor thread (or its {@link Runnable} instance) will have an instance field of type
 * {@link ServerSocket}. The plugin simply attempts to find such a field.
 * 
 * @author Andreas Veithen
 */
@Component(role=ThreadInspectorPlugin.class, hint="acceptor-thread")
public class AcceptorThreadInspectorPlugin implements ThreadInspectorPlugin {
    @Requirement(hint="acceptor-thread")
    private ResourceType resourceType;

    @Requirement
    private ThreadHelper threadHelper;
    
    public boolean isAvailable() {
        return threadHelper.isAvailable();
    }

    public int getPriority() {
        return 1;
    }

    private ServerSocket findSocket(Object object, Class<?> stopClass) {
        Class<?> clazz = object.getClass();
        while (!clazz.equals(stopClass)) {
            for (Field field : clazz.getDeclaredFields()) {
                try {
                    if (ServerSocket.class.isAssignableFrom(field.getType())) {
                        field.setAccessible(true);
                        return (ServerSocket)field.get(object);
                    } else if (field.getName().startsWith("this$")) {
                        field.setAccessible(true);
                        ServerSocket ss = findSocket(field.get(object), Object.class);
                        if (ss != null) {
                            return ss;
                        }
                    }
                } catch (IllegalAccessException ex) {
                    throw new IllegalAccessError(ex.getMessage());
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }
    
    public ThreadDescription getDescription(Thread thread) {
        ServerSocket socket;
        Runnable target = threadHelper.getTarget(thread);
        if (target == null) {
            socket = findSocket(thread, Thread.class);
        } else {
            socket = findSocket(target, Object.class);
        }
        if (socket != null) {
            StringBuilder description = new StringBuilder("TCP acceptor thread; port=");
            description.append(socket.getLocalPort());
            description.append("; name=");
            // Copy & paste code from DefaultThreadInspectorPlugin:
            description.append(thread.getName());
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
            return new ThreadDescription(resourceType, description.toString(), classLoaders);
        } else {
            return null;
        }
    }
}
