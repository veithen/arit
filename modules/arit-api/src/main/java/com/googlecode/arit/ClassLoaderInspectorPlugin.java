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
package com.googlecode.arit;

public interface ClassLoaderInspectorPlugin extends Plugin {
    /**
     * Determine the application to which a given class loader belongs. The implementation should
     * only inspect the given class loader, but not its parents.
     * 
     * @param classLoader
     *            the class loader to inspect
     * @return An identifier for the application, or <code>null</code> if the class loader doesn't
     *         belong to any application. The identifier should be unique, but this is not a strict
     *         requirement. The identifier must be human readable and allow the user to identify the
     *         application. It may be a context path, the name of the application, the location of
     *         the WAR or EAR file, or any other useful identifier.
     */
    ModuleDescription inspect(ClassLoader classLoader);
}
