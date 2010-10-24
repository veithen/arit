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
package com.googlecode.arit.test;

import java.lang.management.ManagementFactory;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.Security;
import java.util.Timer;
import java.util.TimerTask;

import javax.management.JMException;
import javax.management.ObjectName;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.derby.jdbc.EmbeddedDriver;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.googlecode.arit.test.mbean.Echo;
import com.googlecode.arit.test.rmi.HelloWorld;
import com.googlecode.arit.test.rmi.HelloWorldServer;

public class TestServlet extends HttpServlet {
    private final static ThreadLocal<HttpServlet> threadLocal = new ThreadLocal<HttpServlet>();
    
    @SuppressWarnings("unused")
    private HelloWorld helloWorldStub;
    
    @Override
    public void init() throws ServletException {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // Do nothing
            }
        }, 0, 1000);
        new EmbeddedDriver();
        threadLocal.set(this);
        try {
            ManagementFactory.getPlatformMBeanServer().registerMBean(new Echo(), new ObjectName("Test:type=Echo"));
        } catch (JMException ex) {
            throw new ServletException(ex);
        }
        try {
            helloWorldStub = (HelloWorld)UnicastRemoteObject.exportObject(new HelloWorldServer(), 0);
        } catch (RemoteException ex) {
            throw new ServletException(ex);
        }
        Security.addProvider(new BouncyCastleProvider());
    }
}
