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
package com.googlecode.arit.jee;

import org.codehaus.plexus.component.annotations.Component;

import com.googlecode.arit.ModuleType;
import com.googlecode.arit.icon.ImageFormat;

/**
 * Module type used for enterprise applications without distinct class loaders
 * for WARs. Normally, each Web application has its own class loader. However,
 * some application servers (such as WebSphere e.g.) have an option to deploy an
 * EAR with a single class loader. This module type can be used to distinguish
 * this type of deployments from standard J2EE deployments.
 */
@Component(role=ModuleType.class, hint="appwar")
public class AppWarModuleType extends ModuleType {
    public AppWarModuleType() {
        super(ImageFormat.GIF, AppWarModuleType.class.getResource("appwar.gif"));
    }
}
