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

import org.codehaus.plexus.component.annotations.Component;

import com.googlecode.arit.ClassLoaderInspectorProvider;
import com.googlecode.arit.ModuleDescription;
import com.googlecode.arit.rbeans.RBeanFactory;
import com.googlecode.arit.rbeans.RBeanFactoryException;

@Component(role=ClassLoaderInspectorProvider.class, hint="tomcat")
public class TomcatClassLoaderInspectorProvider implements ClassLoaderInspectorProvider {
    private final RBeanFactory rbf;
    
    public TomcatClassLoaderInspectorProvider() {
        RBeanFactory rbf;
        try {
            rbf = new RBeanFactory(WebappClassLoaderRBean.class);
        } catch (RBeanFactoryException ex) {
            rbf = null;
        }
        this.rbf = rbf;
    }
    
    public boolean isAvailable() {
        return rbf != null;
    }

    public ModuleDescription inspect(ClassLoader classLoader) {
        if (rbf.getRBeanInfo(WebappClassLoaderRBean.class).getTargetClass().isInstance(classLoader)) {
            WebappClassLoaderRBean wacl = rbf.createRBean(WebappClassLoaderRBean.class, classLoader);
            ProxyDirContextRBean context = (ProxyDirContextRBean)wacl.getResources();
            // Tomcat removes the DirContext when stopping the application
            return new ModuleDescription(null, context == null ? "<defunct>" : context.getContextName(), classLoader);
        } else {
            return null;
        }
    }
}
