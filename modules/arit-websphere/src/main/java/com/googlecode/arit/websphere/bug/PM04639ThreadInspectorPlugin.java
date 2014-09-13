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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.googlecode.arit.resource.ResourceType;
import com.googlecode.arit.threads.ThreadInspectorPlugin;
import com.googlecode.arit.threads.ThreadResource;
import com.googlecode.arit.threadutils.ThreadHelper;

public class PM04639ThreadInspectorPlugin implements ThreadInspectorPlugin {
    @Autowired
    private ThreadHelper threadHelper;
    
    @Autowired
    @Qualifier("websphere-bug")
    private ResourceType resourceType;
    
    public int getPriority() {
        return 2;
    }

    public boolean isAvailable() {
        return threadHelper.isAvailable();
    }
    
	public ThreadResource getThreadResource(Thread thread) {
        Runnable target = threadHelper.getTarget(thread);
        if (target != null && target.getClass().getName().equals("org.eclipse.jdt.internal.core.search.indexing.IndexManager")) {
			return new ThreadResource(thread, resourceType, "JDT indexer thread (PM04639; Eclipse bug 296343)");
        } else {
            return null;
        }
    }
}
