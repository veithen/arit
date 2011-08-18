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
package com.googlecode.arit.spring;

import java.awt.Color;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.googlecode.arit.ResourceType;

public class ResourceTypeFactory implements FactoryBean<ResourceType>, InitializingBean {
    private String identifier;
    private Color color;
    private ResourceType resourceType;
    
    public Class<?> getObjectType() {
        return ResourceType.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
    public void setColor(Color color) {
        this.color = color;
    }

    public void afterPropertiesSet() throws Exception {
        resourceType = new ResourceType(color, identifier);
    }

    public ResourceType getObject() throws Exception {
        return resourceType;
    }
}
