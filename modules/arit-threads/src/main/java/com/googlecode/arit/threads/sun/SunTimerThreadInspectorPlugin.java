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
package com.googlecode.arit.threads.sun;

import java.util.TimerTask;

import com.googlecode.arit.rbeans.RBeanFactory;
import com.googlecode.arit.rbeans.RBeanFactoryException;
import com.googlecode.arit.threads.AbstractTimerThreadInspectorPlugin;

public class SunTimerThreadInspectorPlugin extends AbstractTimerThreadInspectorPlugin {
    private final RBeanFactory rbf;

    public SunTimerThreadInspectorPlugin() {
        RBeanFactory rbf;
        try {
            rbf = new RBeanFactory(TimerThreadRBean.class);
        } catch (RBeanFactoryException ex) {
            rbf = null;
        }
        this.rbf = rbf;
    }

    public boolean isAvailable() {
        return rbf != null;
    }

    @Override
    protected TimerTask[] getTimerTasks(Thread thread) {
        if (rbf.getRBeanInfo(TimerThreadRBean.class).getTargetClass().isInstance(thread)) {
            return rbf.createRBean(TimerThreadRBean.class, thread).getQueue().getQueue();
        } else {
            return null;
        }
    }
}
