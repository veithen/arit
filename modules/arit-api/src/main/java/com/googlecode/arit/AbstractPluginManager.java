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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;

public abstract class AbstractPluginManager<T extends Plugin> implements BeanFactoryAware, InitializingBean {
    private final Class<T> pluginClass;
    
    private ListableBeanFactory beanFactory;
    
    public AbstractPluginManager(Class<T> pluginClass) {
        this.pluginClass = pluginClass;
    }
    
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ListableBeanFactory)beanFactory;
    }

    public void afterPropertiesSet() throws Exception {
        List<T> availablePlugins = new ArrayList<T>();
        for (T plugin : beanFactory.getBeansOfType(pluginClass).values()) {
            if (plugin.isAvailable()) {
                availablePlugins.add(plugin);
            }
        }
        initialize(availablePlugins);
    }

    protected abstract void initialize(List<T> availablePlugins);
}
