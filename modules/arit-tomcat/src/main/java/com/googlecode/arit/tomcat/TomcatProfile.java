/*
 * Copyright 2010 Andreas Veithen
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
package com.googlecode.arit.tomcat;

import org.apache.catalina.loader.WebappClassLoader;
import org.apache.naming.resources.ProxyDirContext;

import com.googlecode.arit.ServerProfile;

public class TomcatProfile implements ServerProfile {
    public String identifyApplication(ClassLoader classLoader) {
        if (classLoader instanceof WebappClassLoader) {
            WebappClassLoader wacl = (WebappClassLoader)classLoader;
            ProxyDirContext context = (ProxyDirContext)wacl.getResources();
            // Tomcat removes the DirContext when stopping the application
            return context == null ? "<defunct>" : context.getContextName();
        } else {
            return null;
        }
    }
}
