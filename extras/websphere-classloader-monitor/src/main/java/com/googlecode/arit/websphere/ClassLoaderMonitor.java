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
package com.googlecode.arit.websphere;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import com.ibm.ejs.ras.Tr;
import com.ibm.ejs.ras.TraceComponent;
import com.ibm.websphere.runtime.CustomService;
import com.ibm.ws.exception.RuntimeError;
import com.ibm.ws.exception.RuntimeWarning;
import com.ibm.ws.runtime.deploy.DeployedObject;
import com.ibm.ws.runtime.deploy.DeployedObjectEvent;
import com.ibm.ws.runtime.deploy.DeployedObjectListener;
import com.ibm.ws.runtime.service.ApplicationMgr;
import com.ibm.wsspi.runtime.service.WsServiceRegistry;

public class ClassLoaderMonitor implements CustomService, DeployedObjectListener {
    private static final TraceComponent TC = Tr.register(ClassLoaderMonitor.class, "CustomServices", null);
    
    private ApplicationMgr applicationMgr;
    private int createCount;
    private int stopCount;
    private int destroyedCount;
    private long lastDumped;
    private long lastUpdated;
    private List<ClassLoaderInfo> classLoaderInfos;
    private Timer timer;
    
    public void initialize(Properties props) throws Exception {
        applicationMgr = WsServiceRegistry.getService(this, ApplicationMgr.class);
        applicationMgr.addDeployedObjectListener(this);
        classLoaderInfos = new LinkedList<ClassLoaderInfo>();
        timer = new Timer("Class Loader Monitor");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                monitor();
            }
        }, 1000, 1000);
        Tr.info(TC, "Class loader monitor started");
    }

    public void shutdown() throws Exception {
        applicationMgr.removeDeployedObjectListener(this);
        timer.cancel();
        Tr.info(TC, "Class loader monitor stopped");
    }
    
    synchronized void monitor() {
        Iterator<ClassLoaderInfo> it = classLoaderInfos.iterator();
        Map<String,Integer> leakStats = new TreeMap<String,Integer>();
        int count = 0;
        while (it.hasNext()) {
            ClassLoaderInfo classLoaderInfo = it.next();
            if (classLoaderInfo.isStopped()) {
                if (classLoaderInfo.getClassLoader() == null) {
                    it.remove();
                    count++;
                } else {
                    String name = classLoaderInfo.getName();
                    Integer currentCount = leakStats.get(name);
                    if (currentCount == null) {
                        leakStats.put(name, Integer.valueOf(1));
                    } else {
                        leakStats.put(name, currentCount+1);
                    }
                }
            }
        }
        long timestamp = System.currentTimeMillis();
        if (count > 0) {
            destroyedCount += count;
            lastUpdated = System.currentTimeMillis();
        } else if (lastUpdated > lastDumped && timestamp - lastUpdated > 5000) {
            lastDumped = timestamp;
            Tr.info(TC, "Class loader stats: created=" + createCount + "; stopped=" + stopCount + "; destroyed=" + destroyedCount + "; leakStats=" + leakStats);
        }
    }

    public synchronized void stateChanged(DeployedObjectEvent event) throws RuntimeError, RuntimeWarning {
        String state = (String)event.getNewValue();
        DeployedObject deployedObject = event.getDeployedObject();
        ClassLoader classLoader = deployedObject.getClassLoader();
        if (classLoader == null) {
            Tr.error(TC, "DeployedObject#getClassLoader() returned null");
        } else {
            if (state.equals("STARTING")) {
                classLoaderInfos.add(new ClassLoaderInfo(classLoader, deployedObject.getName()));
                createCount++;
                lastUpdated = System.currentTimeMillis();
            } else if (state.equals("DESTROYED")) {
                for (ClassLoaderInfo info : classLoaderInfos) {
                    if (info.getClassLoader() == classLoader) {
                        info.setStopped(true);
                        stopCount++;
                        lastUpdated = System.currentTimeMillis();
                        break;
                    }
                }
            }
        }
    }
}
