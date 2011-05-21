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
package com.googlecode.arit.jul.juli;

import java.util.Iterator;
import java.util.logging.Logger;

import com.googlecode.arit.jul.LoggerEnumerator;

public class ContextLoggerEnumerator implements LoggerEnumerator {
    private final Iterator<Logger> it;
    private Logger logger;
    
    public ContextLoggerEnumerator(ClassLoaderLogInfoRBean logInfo) {
        it = logInfo.getLoggers().values().iterator();
    }

    public boolean next() {
        if (it.hasNext()) {
            logger = it.next();
            return true;
        } else {
            return false;
        }
    }

    public Logger getLogger() {
        return logger;
    }
}
