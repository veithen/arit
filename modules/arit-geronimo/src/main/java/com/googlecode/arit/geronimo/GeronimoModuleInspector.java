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
package com.googlecode.arit.geronimo;

import java.util.List;

import com.github.veithen.rbeans.RBeanFactory;
import com.googlecode.arit.module.ModuleDescription;
import com.googlecode.arit.module.ModuleInspector;
import com.googlecode.arit.module.ModuleStatus;
import com.googlecode.arit.module.ModuleType;

public class GeronimoModuleInspector implements ModuleInspector {
    private final RBeanFactory rbf;
    private final ModuleType warModuleType;

    public GeronimoModuleInspector(RBeanFactory rbf, ModuleType warModuleType) {
        this.rbf = rbf;
        this.warModuleType = warModuleType;
    }

    public List<ModuleDescription> listModules() {
        return null;
    }

    public ModuleDescription inspect(ClassLoader classLoader) {
        if (rbf.getRBeanInfo(JarFileClassLoaderRBean.class).getTargetClass().isInstance(classLoader)) {
            JarFileClassLoaderRBean jfcl = rbf.createRBean(JarFileClassLoaderRBean.class, classLoader);
            // TODO: obviously, not all modules are WARs
            // TODO: we should be able to extract the URL as well
            return new ModuleDescription(warModuleType, jfcl.getId().toString(), classLoader, null,
                    jfcl.isDestroyed() ? ModuleStatus.STOPPED : ModuleStatus.STARTED);
        } else {
            return null;
        }
    }
}
