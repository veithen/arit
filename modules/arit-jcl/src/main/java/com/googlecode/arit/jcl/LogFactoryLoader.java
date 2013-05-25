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
package com.googlecode.arit.jcl;

import java.util.ArrayList;
import java.util.List;

import com.github.veithen.rbeans.RBeanFactory;
import com.github.veithen.rbeans.RBeanFactoryException;
import com.googlecode.arit.AbstractPluginManager;

public class LogFactoryLoader extends AbstractPluginManager<LogFactoryLoaderPlugin> {
    private final List<LogFactoryRef> logFactories = new ArrayList<LogFactoryRef>();
    
    public LogFactoryLoader() {
        super(LogFactoryLoaderPlugin.class);
    }

    public boolean isAvailable() {
        return !logFactories.isEmpty();
    }
    
    @Override
    protected void initialize(List<LogFactoryLoaderPlugin> availablePlugins) {
        for (LogFactoryLoaderPlugin plugin : availablePlugins) {
            RBeanFactory rbf;
            try {
                rbf = new RBeanFactory(plugin.getClassLoader(), LogFactoryRBean.class);
            } catch (RBeanFactoryException ex) {
                continue;
            }
            logFactories.add(new LogFactoryRef(rbf.createRBean(LogFactoryRBean.class), plugin.getDescription()));
        }
    }

    public List<LogFactoryRef> getLogFactories() {
        return logFactories;
    }
}
