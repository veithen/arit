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
package com.googlecode.arit.jul;

import java.util.logging.Handler;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.googlecode.arit.resource.Resource;
import com.googlecode.arit.resource.ResourceScanner.ResourceListener;

import static org.junit.Assert.assertTrue;

public class HandlerResourceScannerTest {
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
    public void testEnumerateHandler() throws Exception {
        Handler handler = new NullHandler();
        Logger logger = Logger.getLogger("");
        logger.addHandler(handler);

		// use final array to enable access from inner class
		final boolean[] found = new boolean[] { false };
        try {
            HandlerResourceScanner resourceScanner = context.getBean(HandlerResourceScanner.class);
            resourceScanner.scanForResources(new ResourceListener() {
				public void onResourceFound(Resource<?> resource) {
					found[0] = true;
				}
			});
        } finally {
            logger.removeHandler(handler);
        }
		assertTrue(found[0]);
    }
    
    /**
	 * Tests that {@link HandlerResourceScanner} works properly in situations where {@link Logger} instances may be
	 * garbage collected (which is possible with Sun JRE 1.6).
	 * 
	 * @throws Exception
	 */
    @Test
    public void testLoggerGarbageCollection() throws Exception {
		HandlerResourceScanner scanner = context.getBean(HandlerResourceScanner.class);
        for (int i=0; i<100; i++) {
            Logger.getLogger("testlogger" + i);
        }
        for (int i=0; i<10; i++) {
			scanner.scanForResources(new ResourceListener() {
				public void onResourceFound(Resource<?> resource) {
					// Just loop
				}
			});
            System.gc();
        }
    }
}
