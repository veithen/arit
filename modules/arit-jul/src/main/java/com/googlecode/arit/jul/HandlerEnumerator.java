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

import java.util.logging.Handler;
import java.util.logging.Logger;

public class HandlerEnumerator {
    private final LoggerEnumerator loggerEnumerator;
    private Handler[] handlers;
    private int handlerIndex;
    
    public HandlerEnumerator(LoggerEnumerator loggerEnumerator) {
        this.loggerEnumerator = loggerEnumerator;
    }
    
    public boolean next() {
        while (true) {
            if (handlers == null) {
                if (loggerEnumerator.next()) {
                    Handler[] handlers = loggerEnumerator.getLogger().getHandlers();
                    // Messages#getHandlers() never returns null
                    if (handlers.length != 0) {
                        this.handlers = handlers;
                        handlerIndex = 0;
                        return true;
                    }
                } else {
                    handlerIndex = -1;
                    return false;
                }
            } else {
                handlerIndex++;
                if (handlerIndex == handlers.length) {
                    handlers = null;
                } else {
                    return true;
                }
            }
        }
    }
    
    public Logger getLogger() {
        return loggerEnumerator.getLogger();
    }
    
    public Handler getHandler() {
        return handlers[handlerIndex];
    }
}
