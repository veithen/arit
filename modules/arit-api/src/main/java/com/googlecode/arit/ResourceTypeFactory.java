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
package com.googlecode.arit;

import java.io.IOException;
import java.net.URL;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.googlecode.arit.icon.ImageFormat;

public class ResourceTypeFactory implements FactoryBean<ResourceType>, InitializingBean {
    private ImageFormat iconFormat;
    private String resource;
    private String identifier;
    private ResourceType resourceType;
    
    public Class<?> getObjectType() {
        return ResourceType.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void setIconFormat(ImageFormat iconFormat) {
        this.iconFormat = iconFormat;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void afterPropertiesSet() throws Exception {
        URL url = ResourceTypeFactory.class.getClassLoader().getResource(resource);
        if (url != null) {
            resourceType = new ResourceType(iconFormat, url, identifier);
        } else {
            throw new IOException("Resource " + resource + " not found");
        }
    }

    public ResourceType getObject() throws Exception {
        return resourceType;
    }
}
