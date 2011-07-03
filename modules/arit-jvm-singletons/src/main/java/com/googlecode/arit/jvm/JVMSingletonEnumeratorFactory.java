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

import javax.annotation.Resource;

import com.googlecode.arit.ResourceEnumerator;
import com.googlecode.arit.ResourceEnumeratorFactory;
import com.googlecode.arit.ResourceType;

public class JVMSingletonEnumeratorFactory implements ResourceEnumeratorFactory {
    @Resource(name="jvm-singleton")
    private ResourceType resourceType;

    private final List<JVMSingleton> singletons = new ArrayList<JVMSingleton>();
    
    public JVMSingletonEnumeratorFactory() {
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

    public ResourceEnumerator createEnumerator() {
        return new JVMSingletonEnumerator(resourceType, singletons);
    }
}
