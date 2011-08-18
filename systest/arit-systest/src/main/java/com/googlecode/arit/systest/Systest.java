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
package com.googlecode.arit.systest;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import junit.framework.TestCase;

import com.googlecode.arit.report.Module;
import com.googlecode.arit.report.Report;
import com.googlecode.arit.report.Resource;

public abstract class Systest extends TestCase {
    private Container container;
    private Application aritApp;
    private Application aritTestApp;
    
    protected abstract Container createContainer(File workDir);
    
    @Override
    protected void setUp() throws Exception {
        File targetDir = new File("target").getAbsoluteFile();
        if (!targetDir.exists()) {
            fail(targetDir + " doesn't exist");
        }
        File tmpDir = new File(targetDir, "systest-tmp");
        tmpDir.mkdir();
        container = createContainer(new File(targetDir, "systest-container"));
        aritApp = new Application(Systest.class.getResource("arit-war.war"), tmpDir, "/arit");
        aritTestApp = new Application(Systest.class.getResource("arit-test-war.war"), tmpDir, "/arit-test");
    }

    public void test() throws Exception {
        container.deployApplication(aritApp);
        String testAppName = container.deployApplication(aritTestApp);
        container.start();
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName reportGeneratorName = (ObjectName)mbs.queryNames(new ObjectName("com.googlecode.arit:type=ReportGenerator"), null).iterator().next();
            Report report = (Report)mbs.invoke(reportGeneratorName, "generateReport", new Object[0], new String[0]);
            List<Module> modules = report.getRootModules();
            Module testAppModule = null;
            for (Module module : modules) {
                if (module.getName().equals(testAppName)) {
                    testAppModule = module;
                    break;
                }
            }
            assertNotNull(testAppModule);
            Set<String> icons = new HashSet<String>();
            for (Resource resource : testAppModule.getResources()) {
                icons.add(resource.getIcon());
            }
            assertTrue(icons.contains("default/threadlocal.png"));
            assertTrue(icons.contains("default/timerthread.png"));
            assertTrue(icons.contains("default/thread.png"));
            assertTrue(icons.contains("default/jce-provider.png"));
            assertTrue(icons.contains("default/mbean.png"));
            assertTrue(icons.contains("default/jdbc-driver.png"));
        } finally {
            container.stop();
        }
    }
}
