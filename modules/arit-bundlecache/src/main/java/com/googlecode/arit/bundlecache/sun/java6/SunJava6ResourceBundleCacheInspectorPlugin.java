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
package com.googlecode.arit.bundlecache.sun.java6;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.github.veithen.rbeans.RBeanFactory;
import com.github.veithen.rbeans.RBeanFactoryException;
import com.googlecode.arit.bundlecache.CachedResourceBundle;
import com.googlecode.arit.bundlecache.ResourceBundleCacheInspectorPlugin;

public class SunJava6ResourceBundleCacheInspectorPlugin implements ResourceBundleCacheInspectorPlugin {
    private final RBeanFactory rbf;
    
    public SunJava6ResourceBundleCacheInspectorPlugin() {
        RBeanFactory rbf;
        try {
            rbf = new RBeanFactory(ResourceBundleRBean.class, CacheKeyRBean.class);
        } catch (RBeanFactoryException ex) {
            rbf = null;
        } catch (ClassCastException ex) {
            // There is a bug in Apache Harmony that triggers a ClassCastException in the JRE
            // TODO: report this bug and apply a proper workaround
            rbf = null;
        }
        this.rbf = rbf;
    }
    
    public boolean isAvailable() {
        return rbf != null;
    }

    public List<CachedResourceBundle> getCachedResourceBundles() {
        Map<?,? extends SoftReference<ResourceBundle>> cache = rbf.createRBean(ResourceBundleRBean.class).getCache();
        List<CachedResourceBundle> result = new ArrayList<CachedResourceBundle>(cache.size());
        for (Map.Entry<?,? extends SoftReference<ResourceBundle>> entry : cache.entrySet()) {
            CacheKeyRBean cacheKey = rbf.createRBean(CacheKeyRBean.class, entry.getKey());
            result.add(new CachedResourceBundle(cacheKey.getLoaderRef().get(), cacheKey.getName(), entry.getValue().get()));
        }
        return result;
    }
}
