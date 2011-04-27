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
package com.googlecode.arit.jmx;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.modelmbean.ModelMBeanNotificationInfo;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Disposable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import com.googlecode.arit.report.Module;
import com.googlecode.arit.report.ReportGenerator;

@Component(role=LeakDetector.class)
public class LeakDetector extends NotificationBroadcasterSupport implements Initializable, Disposable {
    private final static String LEAK_DETECTED = "arit.leak.detected";
    
    @Requirement
    private ReportGenerator reportGenerator;
    
    private Timer timer;
    private final Set<Integer> reportedModules = Collections.synchronizedSet(new HashSet<Integer>());
    private long notificationSequence;
    
    @Override
    public ModelMBeanNotificationInfo[] getNotificationInfo() {
        return new ModelMBeanNotificationInfo[] { new ModelMBeanNotificationInfo(
                new String[] { LEAK_DETECTED }, Notification.class.getName(), "Leak detector notification") };
    }

    public void initialize() throws InitializationException {
        timer = new Timer("LeakDetectorTimer");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runDetection();
            }
        }, 0, 60000);
    }
    
    private void runDetection() {
        for (Module module : reportGenerator.generateReport().getRootModules()) {
            if (module.isStopped()) {
                if (reportedModules.add(module.getId())) {
                    sendNotification(new Notification(LEAK_DETECTED, this, notificationSequence++,
                            "Resource leak detected in application " + module.getName()));
                }
            }
        }
    }
    
    public int getDetectedLeakCount() {
        return reportedModules.size();
    }

    public void dispose() {
        if (timer != null) {
            timer.cancel();
        }
    }
}
