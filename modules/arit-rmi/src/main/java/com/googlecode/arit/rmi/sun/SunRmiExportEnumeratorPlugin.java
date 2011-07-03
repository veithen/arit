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
package com.googlecode.arit.rmi.sun;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.googlecode.arit.rbeans.RBeanFactory;
import com.googlecode.arit.rbeans.RBeanFactoryException;
import com.googlecode.arit.rmi.RmiExportEnumeratorPlugin;

public class SunRmiExportEnumeratorPlugin implements RmiExportEnumeratorPlugin {
    private final Collection<WeakReference<?>> implRefs;
    
    public SunRmiExportEnumeratorPlugin() {
        ObjectTableRBean objectTable;
        try {
            RBeanFactory rbf = new RBeanFactory(ObjectTableRBean.class);
            objectTable = rbf.createRBean(ObjectTableRBean.class);
        } catch (RBeanFactoryException ex) {
            objectTable = null;
        }
        implRefs = objectTable == null ? null : objectTable.getImplementationTable().keySet();
    }
    
    public boolean isAvailable() {
        return implRefs != null;
    }

    public List<Object> getExportedObjects() {
        List<Object> result = new ArrayList<Object>(implRefs.size());
        for (WeakReference<?> ref : implRefs) {
            result.add(ref.get());
        }
        return result;
    }
}
