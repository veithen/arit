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
package com.googlecode.arit.websphere;

import com.googlecode.arit.ServerProfile;
import com.googlecode.arit.rbeans.RBeanFactory;

public class WASProfile implements ServerProfile {
    private final RBeanFactory rbf;
    
    public WASProfile(RBeanFactory rbf) {
        this.rbf = rbf;
    }

    public String identifyApplication(ClassLoader classLoader) {
        if (rbf.getRBeanInfo(CompoundClassLoaderRBean.class).getTargetClass().isInstance(classLoader)) {
            return rbf.createRBean(CompoundClassLoaderRBean.class, classLoader).getName();
        } else {
            return null;
        }
    }
}
