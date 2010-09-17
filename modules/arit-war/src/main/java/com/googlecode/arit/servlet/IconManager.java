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
package com.googlecode.arit.servlet;

import java.util.IdentityHashMap;
import java.util.Map;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import com.googlecode.arit.IconProvider;

public abstract class IconManager<T extends IconProvider> implements Initializable {
    private final Class<T> iconProviderClass;
    
    @Requirement
    private PlexusContainer container;
    
    private Map<String,T> iconProviderMap;
    private Map<T,String> keyMap;

    public IconManager(Class<T> iconProviderClass) {
        this.iconProviderClass = iconProviderClass;
    }

    public void initialize() throws InitializationException {
        try {
            iconProviderMap = container.lookupMap(iconProviderClass);
            keyMap = new IdentityHashMap<T,String>();
            for (Map.Entry<String,T> entry : iconProviderMap.entrySet()) {
                keyMap.put(entry.getValue(), entry.getKey());
            }
        } catch (ComponentLookupException ex) {
            throw new InitializationException("Failed to lookup components with role " + iconProviderClass, ex);
        }
    }
    
    public String getFileName(T iconProvider) {
        return keyMap.get(iconProvider) + "." + iconProvider.getIconFormat().getSuffix();
    }
    
    public T getByFileName(String fileName) {
        int idx = fileName.indexOf('.');
        if (idx == -1) {
            return null;
        } else {
            T iconProvider = iconProviderMap.get(fileName.substring(0, idx));
            if (iconProvider != null && fileName.substring(idx+1).equals(iconProvider.getIconFormat().getSuffix())) {
                return iconProvider;
            } else {
                return null;
            }
        }
    }
}
