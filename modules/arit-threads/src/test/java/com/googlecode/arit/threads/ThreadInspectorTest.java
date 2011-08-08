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
package com.googlecode.arit.threads;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.net.ServerSocket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Exchanger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ThreadInspectorTest {
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
    public void testTimerThread() throws Exception {
        ThreadInspector inspectorManager = context.getBean(ThreadInspector.class);
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
        assertTrue(description.nextClassLoaderReference());
        assertTrue(description.getClassLoaderReferenceDescription().contains(ThreadInspectorTest.class.getName() + "$"));
        timer.cancel();
    }
    
    @Test
    public void testAcceptorThread1() throws Exception {
        ThreadInspector inspectorManager = context.getBean(ThreadInspector.class);
        assertTrue(inspectorManager.isAvailable());
        ServerSocket serverSocket = new ServerSocket(0);
        try {
            Thread thread = new AcceptorThread(serverSocket);
            thread.start();
            ThreadDescription description = inspectorManager.getDescription(thread);
            assertNotNull(description);
            assertTrue(description.getDescription().contains("port " + serverSocket.getLocalPort()));
        } finally {
            serverSocket.close();
        }
    }

    @Test
    public void testAcceptorThread2() throws Exception {
        ThreadInspector inspectorManager = context.getBean(ThreadInspector.class);
        assertTrue(inspectorManager.isAvailable());
        Server server = new Server();
        server.start();
        try {
            ThreadDescription description = inspectorManager.getDescription(server.getThread());
            assertNotNull(description);
            assertTrue(description.getDescription().contains("port " + server.getPort()));
        } finally {
            server.stop();
        }
    }
}
