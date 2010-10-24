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
package com.googlecode.arit.bundlecache.sun;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.codehaus.plexus.component.annotations.Component;

import com.googlecode.arit.bundlecache.CachedResourceBundle;
import com.googlecode.arit.bundlecache.ResourceBundleCacheInspectorPlugin;
import com.googlecode.arit.rbeans.RBeanFactory;
import com.googlecode.arit.rbeans.RBeanFactoryException;

@Component(role=ResourceBundleCacheInspectorPlugin.class, hint="sun")
public class SunResourceBundleCacheInspectorPlugin implements ResourceBundleCacheInspectorPlugin {
    private final RBeanFactory rbf;
    
    public SunResourceBundleCacheInspectorPlugin() {
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
        Map<?,?> cache = rbf.createRBean(ResourceBundleRBean.class).getCache();
        List<CachedResourceBundle> result = new ArrayList<CachedResourceBundle>(cache.size());
        for (Map.Entry<?,?> entry : cache.entrySet()) {
            ResourceCacheKeyRBean cacheKey = rbf.createRBean(ResourceCacheKeyRBean.class, entry.getKey());
            result.add(new CachedResourceBundle(cacheKey.getLoaderRef().get(), cacheKey.getSearchName(), (ResourceBundle)entry.getValue()));
        }
        return result;
    }
}
