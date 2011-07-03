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
package com.googlecode.arit.icon;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.googlecode.arit.icon.variant.IconVariant;

public abstract class IconManager<T extends IconProvider> implements BeanFactoryAware, InitializingBean {
    private final Class<T> iconProviderClass;
    
    private ListableBeanFactory beanFactory;
    
    @Autowired
    private Map<String,IconVariant> variants;
    
    private final Map<T,Icon> iconByProvider = new IdentityHashMap<T,Icon>();
    private final Map<String,Icon> iconByKey = new HashMap<String,Icon>();

    public IconManager(Class<T> iconProviderClass) {
        this.iconProviderClass = iconProviderClass;
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ListableBeanFactory)beanFactory;
    }

    public void afterPropertiesSet() throws Exception {
        Map<String,T> iconProviderMap = beanFactory.getBeansOfType(iconProviderClass);
        for (Map.Entry<String,T> entry : iconProviderMap.entrySet()) {
            String identifier = getIdentifier(entry.getKey(), entry.getValue());
            Icon icon = new Icon(identifier, entry.getValue(), variants);
            iconByProvider.put(entry.getValue(), icon);
            iconByKey.put(identifier, icon);
        }
    }
    
    protected String getIdentifier(String beanName, T bean) {
        return beanName;
    }
    
    public Icon getIcon(T iconProvider) {
        Icon icon = iconByProvider.get(iconProvider);
        if (icon == null) {
            throw new IllegalArgumentException("Icon not found");
        }
        return icon;
    }
    
    public IconImage getByFileName(String fileName) {
        int i = fileName.indexOf('/');
        if (i == -1) {
            return null;
        } else {
            String variantName = fileName.substring(0, i);
            int j = fileName.lastIndexOf('.');
            if (j == -1) {
                return null;
            } else {
                String key = fileName.substring(i+1, j);
                String suffix = fileName.substring(j+1);
                Icon icon = iconByKey.get(key);
                if (icon == null) {
                    return null;
                } else {
                    IconImage image = icon.getIconImage(variantName);
                    if (image != null && image.getData().getFormat().getSuffix().equals(suffix)) {
                        return image;
                    } else {
                        return null;
                    }
                }
            }
        }
    }
}
