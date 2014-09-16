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

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

import javax.management.Notification;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedNotification;
import org.springframework.jmx.export.annotation.ManagedNotifications;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.jmx.export.notification.NotificationPublisher;
import org.springframework.jmx.export.notification.NotificationPublisherAware;

import com.googlecode.arit.report.ClassLoaderLink;
import com.googlecode.arit.report.Module;
import com.googlecode.arit.report.Report;
import com.googlecode.arit.report.ReportGenerator;
import com.googlecode.arit.report.ResourcePresentation;

@ManagedResource(objectName="com.googlecode.arit:type=LeakDetector", description="Detects applications with memory leaks")
@ManagedNotifications(
    @ManagedNotification(name="javax.management.Notification", description="Leak detector notification", notificationTypes={LeakDetector.LEAK_DETECTED})
)
public class LeakDetector implements InitializingBean, DisposableBean, NotificationPublisherAware {
    public final static String LEAK_DETECTED = "arit.leak.detected";
    
    static final Log log = LogFactory.getLog(LeakDetector.class);
    
    @Autowired
    private ReportGenerator reportGenerator;

    private NotificationPublisher notificationPublisher;
    
    private Timer timer;
    private final AtomicReference<Report> lastReport = new AtomicReference<Report>();
    private long notificationSequence;
    
    public void setNotificationPublisher(NotificationPublisher notificationPublisher) {
        this.notificationPublisher = notificationPublisher;
    }

    public void afterPropertiesSet() throws Exception {
        timer = AccessController.doPrivileged(new PrivilegedAction<Timer>() {
            public Timer run() {
                timer = new Timer("LeakDetectorTimer");
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            runDetection();
                        } catch (Throwable ex) {
                            log.error("Leak detection failed", ex);
                        }
                    }
                }, 0, 60000);
                return timer;
            }
        });
    }
    
    private void runDetection() {
        Report report = reportGenerator.generateReport();
        Report previousReport = lastReport.getAndSet(report);
        if (previousReport != null) {
            Map<Integer,Module> reportedModules = new HashMap<Integer,Module>();
            for (Module module : previousReport.getRootModules()) {
                if (module.isStopped()) {
                    reportedModules.put(module.getId(), module);
                }
            }
            for (Module module : report.getRootModules()) {
                if (module.isStopped()) {
                    if (reportedModules.remove(module.getId()) == null) {
                        if (log.isWarnEnabled()) {
                            StringBuilder message = new StringBuilder();
                            message.append("Leak detected:\n");
                            dumpModule(module, message, 0);
                            log.warn(message.toString());
                        }
                        notificationPublisher.sendNotification(new Notification(LEAK_DETECTED, this, notificationSequence++,
                                "Resource leak detected in application " + module.getName()));
                    }
                }
            }
            if (log.isWarnEnabled()) {
                for (Module module : reportedModules.values()) {
                    log.warn("The resource leak previously reported for application " + module.getName() + " (" + module.getId() + ") is now gone");
                }
            }
        }
    }
    
    private void dumpModule(Module module, StringBuilder buffer, int indent) {
        addIndent(buffer, indent);
        buffer.append(module.getName());
        buffer.append(" (");
        buffer.append(module.getId());
        buffer.append(")\n");
        List<ResourcePresentation> resources = module.getResources();
        if (!resources.isEmpty()) {
            addIndent(buffer, indent+1);
            buffer.append("Resources:\n");
            for (ResourcePresentation resource : resources) {
                addIndent(buffer, indent+2);
                buffer.append(resource.getDescription());
                buffer.append('\n');
                for (ClassLoaderLink link : resource.getLinks()) {
                    addIndent(buffer, indent+3);
                    buffer.append("~ ");
                    buffer.append(link.getDescription());
                    buffer.append('\n');
                }
            }
        }
        List<Module> children = module.getChildren();
        if (!children.isEmpty()) {
            addIndent(buffer, indent+1);
            buffer.append("Submodules:\n");
            for (Module child : children) {
                dumpModule(child, buffer, indent+2);
            }
        }
    }
    
    private void addIndent(StringBuilder buffer, int indent) {
        for (int i=0; i<indent*2; i++) {
            buffer.append(' ');
        }
    }
    
    @ManagedAttribute(description="The number of stopped application instances with memory leaks")
    public int getDetectedLeakCount() {
        return getDetectedLeakCount(null);
    }

    @ManagedAttribute(description="The list of all module names monitored by the leak detector")
    public String[] getModuleNames() {
        Set<String> moduleNames = new HashSet<String>();
        for (Module module : lastReport.get().getRootModules()) {
            moduleNames.add(module.getName());
        }
        return moduleNames.toArray(new String[moduleNames.size()]);
    }
    
    @ManagedOperation(description="Get the number of stopped instances with resource leaks for a given application")
    @ManagedOperationParameters(
        @ManagedOperationParameter(name="moduleName", description="The module name for the application")
    )
    public int getDetectedLeakCount(String moduleName) {
        int count = 0;
        for (Module module : lastReport.get().getRootModules()) {
            if (module.isStopped() && (moduleName == null || module.getName().equals(moduleName))) {
                count++;
            }
        }
        return count;
    }
    
    public void destroy() throws Exception {
        if (timer != null) {
            timer.cancel();
        }
    }
}
