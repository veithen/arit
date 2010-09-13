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
package com.googlecode.arit.jetty;

import org.codehaus.plexus.component.annotations.Component;

import com.googlecode.arit.ClassLoaderInspectorPlugin;
import com.googlecode.arit.ModuleDescription;
import com.googlecode.arit.rbeans.RBeanFactory;
import com.googlecode.arit.rbeans.RBeanFactoryException;

@Component(role=ClassLoaderInspectorPlugin.class, hint="jetty")
public class JettyClassLoaderInspectorPlugin implements ClassLoaderInspectorPlugin {
    private final RBeanFactory rbf;
    
    public JettyClassLoaderInspectorPlugin() {
        RBeanFactory rbf;
        try {
            rbf = new RBeanFactory(WebAppClassLoaderRBean.class);
        } catch (RBeanFactoryException ex) {
            rbf = null;
        }
        this.rbf = rbf;
    }
    
    public boolean isAvailable() {
        return rbf != null;
    }

    public ModuleDescription inspect(ClassLoader classLoader) {
        if (rbf.getRBeanInfo(WebAppClassLoaderRBean.class).getTargetClass().isInstance(classLoader)) {
            WebAppClassLoaderRBean wacl = rbf.createRBean(WebAppClassLoaderRBean.class, classLoader);
            WebAppContextRBean context = (WebAppContextRBean)wacl.getContext();
            return new ModuleDescription(null, context.getContextPath(), classLoader);
        } else {
            return null;
        }
    }
}
