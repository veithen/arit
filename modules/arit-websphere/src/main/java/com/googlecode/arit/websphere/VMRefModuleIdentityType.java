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

import org.codehaus.plexus.component.annotations.Component;

import com.googlecode.arit.ModuleIdentityType;
import com.googlecode.arit.icon.ImageFormat;

@Component(role=ModuleIdentityType.class, hint="vmRef")
public class VMRefModuleIdentityType extends ModuleIdentityType {
    public VMRefModuleIdentityType() {
        super("vmRef", ImageFormat.PNG, VMRefModuleIdentityType.class.getResource("tag-red.png"));
    }
}
