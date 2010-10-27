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
package com.googlecode.arit.report;

import java.util.List;

import com.googlecode.arit.ResourceEnumeratorFactory;
import com.googlecode.arit.servlet.log.Message;

public class Report {
    private final List<ResourceEnumeratorFactory> factories;
    private final List<Message> messages;
    private final List<Module> rootModules;
    
    public Report(List<ResourceEnumeratorFactory> factories, List<Message> messages, List<Module> rootModules) {
        this.factories = factories;
        this.messages = messages;
        this.rootModules = rootModules;
    }

    public List<ResourceEnumeratorFactory> getFactories() {
        return factories;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public List<Module> getRootModules() {
        return rootModules;
    }
}
