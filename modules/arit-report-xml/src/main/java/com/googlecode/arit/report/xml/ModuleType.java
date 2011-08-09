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
package com.googlecode.arit.report.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="module", propOrder={"identities", "resources", "children"})
public class ModuleType {
    private Integer id;
    private String name;
    private String type;
    private boolean stopped;
    private String icon;
    private List<IdentityType> identities;
    private List<ResourceType> resources;
    private List<ModuleType> children;

    @XmlAttribute(required=true)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @XmlAttribute(required=true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute(required=true)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlAttribute
    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    @XmlAttribute
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @XmlElement(name="identity")
    public List<IdentityType> getIdentities() {
        return identities;
    }

    public void setIdentities(List<IdentityType> identities) {
        this.identities = identities;
    }

    @XmlElement(name="resource")
    public List<ResourceType> getResources() {
        return resources;
    }

    public void setResources(List<ResourceType> resources) {
        this.resources = resources;
    }

    @XmlElement(name="module")
    public List<ModuleType> getChildren() {
        return children;
    }

    public void setChildren(List<ModuleType> children) {
        this.children = children;
    }
}
