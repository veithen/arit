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
package com.googlecode.arit.jetty;

import java.util.List;

import com.googlecode.arit.ModuleDescription;
import com.googlecode.arit.ModuleInspector;
import com.googlecode.arit.ModuleStatus;
import com.googlecode.arit.ModuleType;
import com.googlecode.arit.rbeans.RBeanFactory;

public class JettyModuleInspector implements ModuleInspector {
    private final RBeanFactory rbf;
    private final ModuleType warModuleType;
    
    public JettyModuleInspector(RBeanFactory rbf, ModuleType warModuleType) {
        this.rbf = rbf;
        this.warModuleType = warModuleType;
    }

    public List<ModuleDescription> listModules() {
        return null;
    }

    public ModuleDescription inspect(ClassLoader classLoader) {
        if (rbf.getRBeanInfo(WebAppClassLoaderRBean.class).getTargetClass().isInstance(classLoader)) {
            WebAppClassLoaderRBean wacl = rbf.createRBean(WebAppClassLoaderRBean.class, classLoader);
            WebAppContextRBean context = (WebAppContextRBean)wacl.getContext();
            // TODO: maybe it's possible to determine the URL?
            return new ModuleDescription(warModuleType, context.getContextPath(), classLoader, null, context.isStopped() ? ModuleStatus.STOPPED : ModuleStatus.STARTED);
        } else {
            return null;
        }
    }
}
