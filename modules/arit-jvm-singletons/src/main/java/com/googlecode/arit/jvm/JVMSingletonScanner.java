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
package com.googlecode.arit.jvm;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.googlecode.arit.resource.ResourceScanner;
import com.googlecode.arit.resource.ResourceType;
import com.googlecode.arit.resource.SimpleResource;

public class JVMSingletonScanner implements ResourceScanner {
    @Autowired
    @Qualifier("jvm-singleton")
    private ResourceType resourceType;

    private final List<JVMSingleton> singletons = new ArrayList<JVMSingleton>();
    
    public JVMSingletonScanner() {
        singletons.add(new JVMSingleton("SecurityManager") {
            @Override
            public Object getInstance() {
                return System.getSecurityManager();
            }
        });
    }
    
    public boolean isAvailable() {
        return true;
    }

    public String getDescription() {
        return "JVM singletons";
    }
    
    public void scanForResources(ResourceListener resourceEventListener) {
    	for (JVMSingleton singleton : singletons) {
            Object instance = singleton.getInstance();
            if (instance != null) {
                SimpleResource<Object> resource = new SimpleResource<Object>(resourceType, instance, singleton.getDescription() + ": " + instance.getClass().getName());
                resource.addClassloaderReference(instance.getClass().getClassLoader(), "Implementation class");
                resourceEventListener.onResourceFound(resource);
            }
		}
    }

}
