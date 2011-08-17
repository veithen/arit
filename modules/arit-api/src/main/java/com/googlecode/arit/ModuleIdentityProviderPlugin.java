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
package com.googlecode.arit;

import java.net.URL;
import java.util.List;

public interface ModuleIdentityProviderPlugin extends Plugin {
    /**
     * Get a list of identities for the given module.
     * 
     * @param url
     *            the root URL of the deployed module or <code>null</code> if the information is not
     *            available; this information should be used with care if <code>moduleStatus</code>
     *            is {@link ModuleStatus#STOPPED}
     * @param classLoader
     *            the class loader of the module
     * @param moduleStatus
     *            the module status
     * @return a list of identities or <code>null</code> if no identities where discovered
     */
    List<ModuleIdentity> getModuleIdentities(URL url, ClassLoader classLoader, ModuleStatus moduleStatus);
}
