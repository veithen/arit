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

import static org.junit.Assert.assertTrue;

import java.util.logging.Handler;
import java.util.logging.Logger;

import org.apache.juli.ClassLoaderLogManager;
import org.junit.Test;

import com.googlecode.arit.jul.NullHandler;

public class LoggingContextResourceEnumeratorTest {
    @Test
    public void test() throws Exception {
        ClassLoaderLogManager logManager = new ClassLoaderLogManager();
        Handler handler = new NullHandler();
        Logger logger = logManager.getLogger("");
        logger.addHandler(handler);
        boolean found = false;
        try {
            LoggingContextResourceEnumeratorFactory factory = new LoggingContextResourceEnumeratorFactory(logManager);
            assertTrue(factory.isAvailable());
            LoggingContextResourceEnumerator enumerator = factory.createEnumerator(com.googlecode.arit.Logger.NULL);
            while (enumerator.nextResource()) {
                while (enumerator.nextClassLoaderReference()) {
                    if (enumerator.getClassLoaderReferenceDescription(null).contains(handler.getClass().getName())) {
                        found = true;
                        // Don't break the loop here so that we test that the loop terminates properly
                    }
                }
            }
        } finally {
            logger.removeHandler(handler);
        }
        assertTrue(found);
    }
}
