/*
 * Copyright 2010-2011,2013 Andreas Veithen
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
package com.googlecode.arit.bundlecache.harmony;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.WeakHashMap;

import com.github.veithen.rbeans.RBeanFactory;
import com.github.veithen.rbeans.RBeanFactoryException;
import com.googlecode.arit.bundlecache.CachedResourceBundle;
import com.googlecode.arit.bundlecache.ResourceBundleCacheInspectorPlugin;

public class HarmonyResourceBundleCacheInspectorPlugin implements ResourceBundleCacheInspectorPlugin {
    private final RBeanFactory rbf;
    
    public HarmonyResourceBundleCacheInspectorPlugin() {
        RBeanFactory rbf;
        try {
            rbf = new RBeanFactory(ResourceBundleRBean.class);
        } catch (RBeanFactoryException ex) {
            rbf = null;
        }
        this.rbf = rbf;
    }
    
    public boolean isAvailable() {
        return rbf != null;
    }

    public List<CachedResourceBundle> getCachedResourceBundles() {
        WeakHashMap<?,Hashtable<String,?>> cache = rbf.createRBean(ResourceBundleRBean.class).getCache();
        List<CachedResourceBundle> result = new ArrayList<CachedResourceBundle>();
        for (Map.Entry<?,Hashtable<String,?>> entry : cache.entrySet()) {
            for (Map.Entry<String,?> entry2 : entry.getValue().entrySet()) {
                Object key = entry.getKey();
                // Some versions of Harmony or JREs derived from Harmony use a String with value "null"
                // instead of a null value as key. This has been observed with IBM Java 6.0 SR1.
                if (key != null && !key.equals("null")) {
                    Object value = entry2.getValue();
                    result.add(new CachedResourceBundle((ClassLoader)key, entry2.getKey(),
                            (ResourceBundle)(value instanceof Reference<?> ? ((Reference<?>)value).get() : value)));
                }
            }
        }
        return result;
    }
}
