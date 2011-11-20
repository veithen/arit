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
package com.ibm.wsspi.runtime.component;

import com.ibm.ws.exception.ComponentDisabledException;
import com.ibm.ws.exception.ConfigurationError;
import com.ibm.ws.exception.ConfigurationWarning;
import com.ibm.ws.exception.RuntimeError;
import com.ibm.ws.exception.RuntimeWarning;

public interface WsComponent {
    String STATE = "state";
    String INITIALIZING = "INITIALIZING";
    String INITIALIZED = "INITIALIZED";
    String STARTING = "STARTING";
    String STARTED = "STARTED";
    String STOPPING = "STOPPING";
    String STOPPED = "STOPPED";
    String DESTROYING = "DESTROYING";
    String DESTROYED = "DESTROYED";
    String ERROR = "ERROR";
    
    String getName();
    String getState();
    void initialize(Object object) throws ComponentDisabledException, ConfigurationWarning, ConfigurationError;
    void destroy();
    void start() throws RuntimeError, RuntimeWarning;
    void stop();
}
