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
package com.googlecode.arit.bundlecache.sun.java5;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.googlecode.arit.bundlecache.CachedResourceBundle;
import com.googlecode.arit.bundlecache.ResourceBundleCacheInspectorPlugin;
import com.googlecode.arit.rbeans.RBeanFactory;
import com.googlecode.arit.rbeans.RBeanFactoryException;

public class SunJava5ResourceBundleCacheInspectorPlugin implements ResourceBundleCacheInspectorPlugin {
    private final RBeanFactory rbf;
    
    public SunJava5ResourceBundleCacheInspectorPlugin() {
        RBeanFactory rbf;
        try {
            rbf = new RBeanFactory(ResourceBundleRBean.class, ResourceCacheKeyRBean.class);
        } catch (RBeanFactoryException ex) {
            rbf = null;
        }
        this.rbf = rbf;
    }
    
    public boolean isAvailable() {
        return rbf != null;
    }

    public List<CachedResourceBundle> getCachedResourceBundles() {
        Map<?,Object> cache = rbf.createRBean(ResourceBundleRBean.class).getCache();
        List<CachedResourceBundle> result = new ArrayList<CachedResourceBundle>(cache.size());
        for (Map.Entry<?,Object> entry : cache.entrySet()) {
            Object value = entry.getValue();
            // Java 5 uses an instance of Object (NOT_FOUND in the code) for negative caching
            if (value instanceof ResourceBundle) {
                ResourceCacheKeyRBean cacheKey = rbf.createRBean(ResourceCacheKeyRBean.class, entry.getKey());
                result.add(new CachedResourceBundle(cacheKey.getLoaderRef().get(), cacheKey.getSearchName(), (ResourceBundle)value));
            }
        }
        return result;
    }
}
