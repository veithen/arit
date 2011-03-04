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
package com.googlecode.arit.websphere;

import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

import com.googlecode.arit.ModuleIdentity;
import com.googlecode.arit.ModuleIdentityProviderPlugin;
import com.googlecode.arit.ModuleIdentityType;
import com.googlecode.arit.rbeans.RBeanFactory;
import com.googlecode.arit.rbeans.RBeanFactoryException;

/**
 * Extracts the (IBM specific) <code>vmRef</code> attribute from the class loader. This may be used
 * later to correlate the output of Arit with e.g. a heap dump.
 * 
 * @author Andreas Veithen
 */
@Component(role=ModuleIdentityProviderPlugin.class, hint="vmRef")
public class VMRefModuleIdentityProvider implements ModuleIdentityProviderPlugin {
    @Requirement(hint="vmRef")
    private ModuleIdentityType identityType;
    
    private final RBeanFactory rbf;
    
    public VMRefModuleIdentityProvider() {
        RBeanFactory rbf;
        try {
            rbf = new RBeanFactory(ClassLoaderRBean.class);
        } catch (RBeanFactoryException ex) {
            rbf = null;
        }
        this.rbf = rbf;
    }

    public boolean isAvailable() {
        return rbf != null;
    }

    public List<ModuleIdentity> getModuleIdentities(URL url, ClassLoader classLoader) {
        return Collections.singletonList(new ModuleIdentity(identityType, String.valueOf(rbf.createRBean(ClassLoaderRBean.class, classLoader).getVMRef())));
    }
}
