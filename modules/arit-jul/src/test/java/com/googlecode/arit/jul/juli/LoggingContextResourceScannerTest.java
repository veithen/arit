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
package com.googlecode.arit.jul.juli;

import java.util.logging.Handler;
import java.util.logging.Logger;

import org.apache.juli.ClassLoaderLogManager;
import org.junit.Test;

import com.googlecode.arit.jul.NullHandler;
import com.googlecode.arit.resource.ClassLoaderReference;
import com.googlecode.arit.resource.Resource;
import com.googlecode.arit.resource.ResourceScanner.ResourceListener;

import static org.junit.Assert.assertTrue;

public class LoggingContextResourceScannerTest {
    @Test
    public void test() throws Exception {
        ClassLoaderLogManager logManager = new ClassLoaderLogManager();
		final Handler handler = new NullHandler();
        Logger logger = logManager.getLogger("");
        logger.addHandler(handler);
		final boolean[] found = { false };
        try {
			LoggingContextResourceScanner scanner = new LoggingContextResourceScanner(logManager);
			assertTrue(scanner.isAvailable());
			ResourceListener resourceEventListener = new ResourceListener() {
				public void onResourceFound(Resource<?> resource) {
					for (ClassLoaderReference clRef : resource.getClassLoaderReferences()) {
						if (clRef.getDescription(null).contains(handler.getClass().getName())) {
							found[0] = true;
						}
					}

				}
			};
			scanner.scanForResources(resourceEventListener);
        } finally {
            logger.removeHandler(handler);
        }
		assertTrue(found[0]);
    }
}
