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
package com.googlecode.arit.jul;

import java.util.Enumeration;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class LogManagerLoggerEnumerator implements LoggerEnumerator {
    private final LogManager logManager;
    private final Enumeration<String> loggerNames;
    private Logger logger;
    
    public LogManagerLoggerEnumerator(LogManager logManager) {
        this.logManager = logManager;
        loggerNames = logManager.getLoggerNames();
    }

    public boolean next() {
        if (loggerNames.hasMoreElements()) {
            logger = logManager.getLogger(loggerNames.nextElement());
            return true;
        } else {
            return false;
        }
    }

    public Logger getLogger() {
        return logger;
    }
}
