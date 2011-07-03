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
package com.googlecode.arit.jcl;

public class DefaultLogFactoryLoaderPlugin implements LogFactoryLoaderPlugin {
    public boolean isAvailable() {
        return true;
    }

    public String getDescription() {
        return "container";
    }

    public ClassLoader getClassLoader() {
        // We need to get the parent class loader because Arit is packaged
        // with commons-logging itself. Indeed:
        //  * This makes sure that the plugin is only enabled if
        //    commons-logging is actually deployed in the container.
        //  * This makes sure that we pick up the right commons-logging
        //    if the container uses parent-last class loading (e.g. Tomcat).
        return DefaultLogFactoryLoaderPlugin.class.getClassLoader().getParent();
    }
}
