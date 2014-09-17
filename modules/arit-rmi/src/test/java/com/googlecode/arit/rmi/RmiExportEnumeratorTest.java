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
package com.googlecode.arit.rmi;

import static org.junit.Assert.assertTrue;

import java.rmi.server.UnicastRemoteObject;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.googlecode.arit.Messages;
import com.googlecode.arit.resource.ResourceEnumerator;

public class RmiExportEnumeratorTest {
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
        RmiExportEnumeratorFactory enumeratorFactory = context.getBean(RmiExportEnumeratorFactory.class);
        assertTrue(enumeratorFactory.isAvailable());
        HelloWorldServer server = new HelloWorldServer();
        UnicastRemoteObject.exportObject(server, 0);
        try {
            ResourceEnumerator enumerator = enumeratorFactory.createEnumerator(Messages.NULL);
            boolean found = false;
            while (enumerator.nextResource()) {
                if (enumerator.getResourceObject() == server) {
                    found = true;
                    break;
                }
            }
            assertTrue(found);
        } finally {
            UnicastRemoteObject.unexportObject(server, true);
        }
    }
}
