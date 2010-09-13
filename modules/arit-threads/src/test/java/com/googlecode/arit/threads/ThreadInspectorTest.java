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
package com.googlecode.arit.threads;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Exchanger;

import org.codehaus.plexus.PlexusTestCase;

public class ThreadInspectorTest extends PlexusTestCase {
    public void test() throws Exception {
        ThreadInspector inspectorManager = lookup(ThreadInspector.class);
        assertTrue(inspectorManager.isAvailable());
        Timer timer = new Timer();
        // java.util.Timer doesn't offer any method to get the timer thread; thus
        // we need to get this information from within a TimerTask.
        final Exchanger<Thread> exchanger = new Exchanger<Thread>();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    exchanger.exchange(Thread.currentThread());
                } catch (InterruptedException ex) {
                    Thread.interrupted();
                }
            }
        }, 0);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                
            }
        }, 1000, 1000);
        Thread thread = exchanger.exchange(null);
        ThreadDescription description = inspectorManager.getDescription(thread);
        assertTrue(description.getDescription().contains(ThreadInspectorTest.class.getName() + "$"));
        timer.cancel();
    }
}
