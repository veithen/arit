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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.googlecode.arit.ModuleInspector;
import com.googlecode.arit.ModuleInspectorPlugin;
import com.googlecode.arit.ModuleType;
import com.googlecode.arit.rbeans.RBeanFactory;
import com.googlecode.arit.rbeans.RBeanFactoryException;

public class GeronimoModuleInspectorPlugin implements ModuleInspectorPlugin {
    private final RBeanFactory rbf;
    
    @Autowired
    @Qualifier("war")
    private ModuleType warModuleType;
    
    public GeronimoModuleInspectorPlugin() {
        RBeanFactory rbf;
        try {
            rbf = new RBeanFactory(JarFileClassLoaderRBean.class);
        } catch (RBeanFactoryException ex) {
            rbf = null;
        }
        this.rbf = rbf;
    }
    
    public boolean isAvailable() {
        return rbf != null;
    }

    public ModuleInspector createModuleInspector() {
        return new GeronimoModuleInspector(rbf, warModuleType);
    }
}
