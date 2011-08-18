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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public class ResourceTypePostProcessor implements BeanFactoryPostProcessor {
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        List<BeanDefinition> resourceTypeDefinitions = new ArrayList<BeanDefinition>();
        List<String> resourceTypeIdentifiers = new ArrayList<String>();
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
            if (beanDefinition.getBeanClassName().equals(ResourceTypeFactory.class.getName())) {
                resourceTypeDefinitions.add(beanDefinition);
                resourceTypeIdentifiers.add((String)beanDefinition.getPropertyValues().getPropertyValue("identifier").getValue());
            }
        }
        Map<String,Color> colors = ColorPaletteGenerator.assignColors(resourceTypeIdentifiers);
        for (BeanDefinition beanDefinition : resourceTypeDefinitions) {
            MutablePropertyValues properties = beanDefinition.getPropertyValues();
            String identifier = (String)properties.getPropertyValue("identifier").getValue();
            properties.add("color", colors.get(identifier));
        }
    }
}
