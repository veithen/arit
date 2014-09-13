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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.googlecode.arit.resource.ResourceType;
import com.googlecode.arit.threadutils.ThreadHelper;

/**
 * Plugin that attempts to identify TCP acceptor threads. It is based on the idea that in most
 * cases, an acceptor thread (or its {@link Runnable} instance) will have an instance field of type
 * {@link ServerSocket}. The plugin simply attempts to find such a field.
 * 
 * @author Andreas Veithen
 */
public class AcceptorThreadInspectorPlugin implements ThreadInspectorPlugin {
    @Autowired
    @Qualifier("acceptor-thread")
    private ResourceType resourceType;

    @Autowired
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
    
	public ThreadResource getThreadResource(Thread thread) {
        ServerSocket socket;
        Runnable target = threadHelper.getTarget(thread);
        if (target == null) {
            socket = findSocket(thread, Thread.class);
        } else {
            socket = findSocket(target, Object.class);
        }
        if (socket != null) {
			return new ThreadResource(thread, resourceType, "TCP acceptor thread (port " + socket.getLocalPort() + ")");
        } else {
            return null;
        }
    }
}
