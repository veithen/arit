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
package com.googlecode.arit.websphere.bug;

import java.util.Collections;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

import com.googlecode.arit.ResourceType;
import com.googlecode.arit.threads.ThreadDescription;
import com.googlecode.arit.threads.ThreadInspectorPlugin;
import com.googlecode.arit.threadutils.ThreadHelper;

@Component(role=ThreadInspectorPlugin.class, hint="PM04639")
public class PM04639ThreadInspectorPlugin implements ThreadInspectorPlugin {
    @Requirement
    private ThreadHelper threadHelper;
    
    @Requirement(hint="websphere-bug")
    private ResourceType resourceType;
    
    public int getPriority() {
        return 2;
    }

    public boolean isAvailable() {
        return threadHelper.isAvailable();
    }
    
    public ThreadDescription getDescription(Thread thread) {
        Runnable target = threadHelper.getTarget(thread);
        if (target != null && target.getClass().getName().equals("org.eclipse.jdt.internal.core.search.indexing.IndexManager")) {
            return new ThreadDescription(resourceType, "PM04639 (Eclipse bug 296343): JDT indexer thread", Collections.<ClassLoader>emptySet());
        } else {
            return null;
        }
    }
}
