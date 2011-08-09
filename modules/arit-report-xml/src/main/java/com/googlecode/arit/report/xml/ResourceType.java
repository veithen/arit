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

@XmlType(name="resource")
public class ResourceType {
    private String type;
    private String description;
    private List<ClassLoaderLinkType> links;
    
    @XmlAttribute(required=true)
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }

    @XmlAttribute(required=true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement(name="link")
    public List<ClassLoaderLinkType> getLinks() {
        return links;
    }

    public void setLinks(List<ClassLoaderLinkType> links) {
        this.links = links;
    }
}
