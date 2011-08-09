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

public class Resource implements Serializable {
    private static final long serialVersionUID = -3572748920334331705L;
    
    private final Integer id;
    private final String icon;
    private final String type;
    private final String description;
    private final List<ClassLoaderLink> links = new ArrayList<ClassLoaderLink>();

    public Resource(Integer id, String icon, String type, String description) {
        this.id = id;
        this.icon = icon;
        this.type = type;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public String getIcon() {
        return icon;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public List<ClassLoaderLink> getLinks() {
        return links;
    }
}
