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
package com.googlecode.arit.axis2;

import com.googlecode.arit.threadlocals.ThreadLocalValueDescription;

public class AxisServiceThreadLocalValueDescription implements ThreadLocalValueDescription {
    private final String name;
    private final ClassLoader classLoader;
    
    public AxisServiceThreadLocalValueDescription(String name, ClassLoader classLoader) {
        this.name = name;
        this.classLoader = classLoader;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public String getDescription() {
        return "AxisService description for " + name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AxisServiceThreadLocalValueDescription) {
            return ((AxisServiceThreadLocalValueDescription)obj).name.equals(name);
        } else {
            return false;
        }
    }
}
