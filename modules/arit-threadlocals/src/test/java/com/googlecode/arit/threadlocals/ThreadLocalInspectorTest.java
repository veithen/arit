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
package com.googlecode.arit.threadlocals;

import java.util.List;
import java.util.Map;

import org.codehaus.plexus.PlexusTestCase;

public class ThreadLocalInspectorTest extends PlexusTestCase {
    public void test() throws Exception {
        List<ThreadLocalInspector> inspectors = getContainer().lookupList(ThreadLocalInspector.class);
        ThreadLocalInspector inspector = null;
        for (ThreadLocalInspector candidate : inspectors) {
            if (candidate.isAvailable()) {
                inspector = candidate;
                break;
            }
        }
        assertNotNull(inspector);
        ThreadLocal<String> threadLocal = new ThreadLocal<String>();
        threadLocal.set("test");
        Map<ThreadLocal<?>,Object> threadLocalMap = inspector.getThreadLocalMap(Thread.currentThread());
        assertEquals("test", threadLocalMap.get(threadLocal));
    }
}
