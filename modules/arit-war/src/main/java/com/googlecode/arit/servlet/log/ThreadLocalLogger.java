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
package com.googlecode.arit.servlet.log;

import java.util.List;

import org.codehaus.plexus.logging.AbstractLogger;
import org.codehaus.plexus.logging.Logger;

import com.googlecode.arit.report.Message;

public class ThreadLocalLogger extends AbstractLogger {
    private static ThreadLocal<List<Message>> targetTL = new ThreadLocal<List<Message>>();
    
    public ThreadLocalLogger(int threshold, String name) {
        super(threshold, name);
    }
    
    public static void setTarget(List<Message> target) {
        targetTL.set(target);
    }
    
    private void log(String message) {
        List<Message> target = targetTL.get();
        if (target != null) {
            target.add(new Message(message));
        }
    }

    public Logger getChildLogger(String name) {
        return this;
    }

    public void debug(String message, Throwable throwable) {
        log(message);
    }

    public void error(String message, Throwable throwable) {
        log(message);
    }

    public void fatalError(String message, Throwable throwable) {
        log(message);
    }

    public void info(String message, Throwable throwable) {
        log(message);
    }

    public void warn(String message, Throwable throwable) {
        log(message);
    }
}
