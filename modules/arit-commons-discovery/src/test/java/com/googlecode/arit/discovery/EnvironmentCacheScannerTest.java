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
package com.googlecode.arit.discovery;

import org.apache.commons.discovery.tools.DiscoverSingleton;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.googlecode.arit.resource.Resource;
import com.googlecode.arit.resource.ResourceScanner.ResourceListener;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class EnvironmentCacheScannerTest {
    private static ClassPathXmlApplicationContext context;
    
    @BeforeClass
    public static void initContext() {
        context = new ClassPathXmlApplicationContext("arit-appcontext.xml");
    }
    
    @AfterClass
    public static void destroyContext() {
        context.destroy();
    }
    
    @Test
    public void test() throws Exception {
        System.setProperty(DummyProvider.class.getName(), DummyProviderImpl.class.getName());
		final Object instance = DiscoverSingleton.find(DummyProvider.class);
		EnvironmentCacheScanner enumeratorFactory = context.getBean(EnvironmentCacheScanner.class);
        assertTrue(enumeratorFactory.isAvailable());

		final boolean[] found = { false };
		enumeratorFactory.scanForResources(new ResourceListener() {

			public void onResourceFound(Resource<?> resource) {
				assertSame(instance, resource.getResourceObject());
				found[0] = true;
			}
		});
		assertTrue(found[0]);
    }
}
