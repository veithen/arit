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
package com.googlecode.arit.report;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Module implements Serializable {
    private static final long serialVersionUID = -6336260718480054351L;
    
    private final Integer id;
    private final String name;
    private final String type;
    private final boolean stopped;
    private String icon;
    private final List<Identity> identities = new ArrayList<Identity>();
    private final List<ResourceTypePresentation> resources = new ArrayList<ResourceTypePresentation>();
    private Module parent;
    private final List<Module> children = new ArrayList<Module>();
    
    public Module(Integer id, String name, String type, boolean stopped) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.stopped = stopped;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isStopped() {
        return stopped;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<Identity> getIdentities() {
        return identities;
    }

    public void addIdentity(Identity identity) {
        identities.add(identity);
    }

    public List<ResourceTypePresentation> getResources() {
        return resources;
    }
    
    public void addChild(Module child) {
        if (child.parent != null) {
            throw new IllegalStateException();
        }
        child.parent = this;
        children.add(child);
    }

    public Module getParent() {
        return parent;
    }

    public List<Module> getChildren() {
        return children;
    }
}
