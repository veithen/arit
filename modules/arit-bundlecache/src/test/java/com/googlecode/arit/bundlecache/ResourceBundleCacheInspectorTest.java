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
package com.googlecode.arit.bundlecache;

import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.googlecode.arit.resource.ResourceScanningConfig;

import static org.junit.Assert.assertTrue;

public class ResourceBundleCacheInspectorTest {
    private static ClassPathXmlApplicationContext context;
    
	public static class ResourceScanningConfigImpl implements ResourceScanningConfig {
		public boolean includeGarbageCollectableResources() {
			return true;
		}

	}

    @BeforeClass
    public static void initContext() {
        context = new ClassPathXmlApplicationContext("applicationContext-test.xml");
    }
    
    @AfterClass
    public static void destroyContext() {
        context.destroy();
    }
    
    @Test
    public void test() throws Exception {
        ResourceBundleCacheInspector inspector = context.getBean(ResourceBundleCacheInspector.class);
        assertTrue(inspector.isAvailable());
        ResourceBundle bundle = ResourceBundle.getBundle("com.googlecode.arit.bundlecache.bundle");
        List<CachedResourceBundle> cachedBundles = inspector.getCachedResourceBundles();
        boolean found = false;
        for (CachedResourceBundle cachedBundle : cachedBundles) {
            if (cachedBundle.getBundle() == bundle) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    public void testNotFound() throws Exception {
        ResourceBundleCacheInspector inspector = context.getBean(ResourceBundleCacheInspector.class);
        assertTrue(inspector.isAvailable());
        try {
            ResourceBundle.getBundle("non.existing.bundle");
        } catch (MissingResourceException ex) {
            // This exception is expected; continue
        }
        // Some JREs use a special value for negative caching; the inspector must be prepared
        // to handle this, i.e. it must not throw any exception here
        inspector.getCachedResourceBundles();
    }
}
