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
package com.googlecode.arit.rmi.harmony;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.codehaus.plexus.component.annotations.Component;

import com.googlecode.arit.rbeans.RBeanFactory;
import com.googlecode.arit.rbeans.RBeanFactoryException;
import com.googlecode.arit.rmi.RmiInspectorPlugin;

@Component(role=RmiInspectorPlugin.class, hint="harmony")
public class HarmonyRmiInspectorPlugin implements RmiInspectorPlugin {
    private final Collection<WeakReference<?>> refs;
    
    public HarmonyRmiInspectorPlugin() {
        ExportManagerRBean exportManager;
        try {
            RBeanFactory rbf = new RBeanFactory(ExportManagerRBean.class);
            exportManager = rbf.createRBean(ExportManagerRBean.class);
        } catch (RBeanFactoryException ex) {
            exportManager = null;
        }
        refs = exportManager == null ? null : exportManager.getExportedObjects().getRefTable().keySet();
    }
    
    public boolean isAvailable() {
        return refs != null;
    }

    public List<Object> getExportedObjects() {
        List<Object> result = new ArrayList<Object>(refs.size());
        for (WeakReference<?> ref : refs) {
            result.add(ref.get());
        }
        return result;
    }
}
