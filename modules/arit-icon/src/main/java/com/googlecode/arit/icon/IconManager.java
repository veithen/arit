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
package com.googlecode.arit.icon;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import com.googlecode.arit.icon.variant.IconVariant;

public abstract class IconManager<T extends IconProvider> implements Initializable {
    private final Class<T> iconProviderClass;
    
    @Requirement
    private PlexusContainer container;
    
    @Requirement(role=IconVariant.class)
    private Map<String,IconVariant> variants;
    
    private final Map<T,Icon> iconByProvider = new IdentityHashMap<T,Icon>();
    private final Map<String,Icon> iconByKey = new HashMap<String,Icon>();

    public IconManager(Class<T> iconProviderClass) {
        this.iconProviderClass = iconProviderClass;
    }

    public void initialize() throws InitializationException {
        try {
            Map<String,T> iconProviderMap = container.lookupMap(iconProviderClass);
            for (Map.Entry<String,T> entry : iconProviderMap.entrySet()) {
                Icon icon = new Icon(entry.getKey(), entry.getValue(), variants);
                iconByProvider.put(entry.getValue(), icon);
                iconByKey.put(entry.getKey(), icon);
            }
        } catch (ComponentLookupException ex) {
            throw new InitializationException("Failed to lookup components with role " + iconProviderClass, ex);
        }
    }
    
    public Icon getIcon(T iconProvider) {
        return iconByProvider.get(iconProvider);
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
