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
package com.googlecode.arit.mbeans;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.codehaus.plexus.PlexusTestCase;

public class MBeanServerInspectorTest extends PlexusTestCase {
    public void test() throws Exception {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        Object mbean = new Dummy();
        ObjectName name = new ObjectName("Test:type=Dummy");
        mbs.registerMBean(mbean, name);
        boolean found = false;
        try {
            for (MBeanServerInspectorPlugin inspector : getContainer().lookupList(MBeanServerInspectorPlugin.class)) {
                if (inspector.isAvailable()) {
                    MBeanRepository repository = inspector.inspect(mbs);
                    if (repository != null) {
                        assertSame(mbean, repository.retrieve(name));
                        found = true;
                    }
                }
            }
        } finally {
            mbs.unregisterMBean(name);
        }
        assertTrue(found);
    }
}
