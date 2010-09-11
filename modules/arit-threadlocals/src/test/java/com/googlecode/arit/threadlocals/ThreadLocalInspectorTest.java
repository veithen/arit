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

import org.junit.Assert;
import org.junit.Test;

import com.googlecode.arit.ProviderFinder;
import com.googlecode.arit.threadlocals.ThreadLocalInspector;

public class ThreadLocalInspectorTest {
    @Test
    public void test() {
        List<ThreadLocalInspector> inspectors = ProviderFinder.find(ThreadLocalInspector.class);
        Assert.assertEquals(1, inspectors.size());
        ThreadLocalInspector inspector = inspectors.get(0);
        ThreadLocal<String> threadLocal = new ThreadLocal<String>();
        threadLocal.set("test");
        Map<ThreadLocal<?>,Object> threadLocalMap = inspector.getThreadLocalMap(Thread.currentThread());
        Assert.assertEquals("test", threadLocalMap.get(threadLocal));
    }
}
