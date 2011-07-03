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
package com.googlecode.arit.bundlecache.harmony;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.WeakHashMap;

import com.googlecode.arit.bundlecache.CachedResourceBundle;
import com.googlecode.arit.bundlecache.ResourceBundleCacheInspectorPlugin;
import com.googlecode.arit.rbeans.RBeanFactory;
import com.googlecode.arit.rbeans.RBeanFactoryException;

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
        WeakHashMap<Object,Hashtable<String,ResourceBundle>> cache = rbf.createRBean(ResourceBundleRBean.class).getCache();
        List<CachedResourceBundle> result = new ArrayList<CachedResourceBundle>();
        for (Map.Entry<Object,Hashtable<String,ResourceBundle>> entry : cache.entrySet()) {
            for (Map.Entry<String,ResourceBundle> entry2 : entry.getValue().entrySet()) {
                result.add(new CachedResourceBundle((ClassLoader)entry.getKey(), entry2.getKey(), entry2.getValue()));
            }
        }
        return result;
    }
}
