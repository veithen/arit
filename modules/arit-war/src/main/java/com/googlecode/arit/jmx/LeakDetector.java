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

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedNotification;
import org.springframework.jmx.export.annotation.ManagedNotifications;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.jmx.export.notification.NotificationPublisher;
import org.springframework.jmx.export.notification.NotificationPublisherAware;

import com.googlecode.arit.report.Module;
import com.googlecode.arit.report.ReportGenerator;

@ManagedResource(objectName="com.googlecode.arit:type=LeakDetector", description="Detects applications with memory leaks")
@ManagedNotifications(
    @ManagedNotification(name="javax.management.Notification", description="Leak detector notification", notificationTypes={LeakDetector.LEAK_DETECTED})
)
public class LeakDetector implements InitializingBean, DisposableBean, NotificationPublisherAware {
    public final static String LEAK_DETECTED = "arit.leak.detected";
    
    @Autowired
    private ReportGenerator reportGenerator;

    private NotificationPublisher notificationPublisher;
    
    private Timer timer;
    private final Set<Integer> reportedModules = Collections.synchronizedSet(new HashSet<Integer>());
    private long notificationSequence;
    
    public void setNotificationPublisher(NotificationPublisher notificationPublisher) {
        this.notificationPublisher = notificationPublisher;
    }

    public void afterPropertiesSet() throws Exception {
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
                    notificationPublisher.sendNotification(new Notification(LEAK_DETECTED, this, notificationSequence++,
                            "Resource leak detected in application " + module.getName()));
                }
            }
        }
    }
    
    @ManagedAttribute(description="The number of stopped application instances with memory leaks")
    public int getDetectedLeakCount() {
        return reportedModules.size();
    }

    public void destroy() throws Exception {
        if (timer != null) {
            timer.cancel();
        }
    }
}
