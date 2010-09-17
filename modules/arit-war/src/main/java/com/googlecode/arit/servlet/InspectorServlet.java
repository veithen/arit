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
package com.googlecode.arit.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

import com.googlecode.arit.ModuleDescription;
import com.googlecode.arit.ModuleInspector;
import com.googlecode.arit.ResourceEnumerator;
import com.googlecode.arit.ResourceEnumeratorFactory;
import com.googlecode.arit.ServerContext;
import com.googlecode.arit.servlet.log.Message;
import com.googlecode.arit.servlet.log.ThreadLocalLogger;

@Component(role=HttpServlet.class, hint="InspectorServlet")
public class InspectorServlet extends HttpServlet {
    @Requirement
    private ModuleInspectorFactory moduleInspectorFactory;
    
    @Requirement(role=ResourceEnumeratorFactory.class)
    private List<ResourceEnumeratorFactory> resourceEnumeratorFactories;
    
    private List<ResourceEnumeratorFactory> availableResourceEnumeratorFactories = new ArrayList<ResourceEnumeratorFactory>();
    private List<ResourceEnumeratorFactory> unavailableResourceEnumeratorFactories = new ArrayList<ResourceEnumeratorFactory>();
    
    private ServerContext getServerContext() {
        return new ServerContext(getServletContext(), getClass().getClassLoader());
    }
    
    @Override
    public void init() throws ServletException {
        for (ResourceEnumeratorFactory resourceEnumeratorFactory : resourceEnumeratorFactories) {
            if (resourceEnumeratorFactory.isAvailable()) {
                availableResourceEnumeratorFactories.add(resourceEnumeratorFactory);
            } else {
                unavailableResourceEnumeratorFactories.add(resourceEnumeratorFactory);
            }
        }
    }

    @Override
    public void destroy() {
        availableResourceEnumeratorFactories.clear();
        unavailableResourceEnumeratorFactories.clear();
    }
    
    private Module getModule(ModuleInspector moduleInspector, Map<ClassLoader,Module> moduleMap, ClassLoader classLoader) {
        if (moduleMap.containsKey(classLoader)) {
            return moduleMap.get(classLoader);
        } else {
            ModuleDescription desc = moduleInspector.inspect(classLoader);
            Module module;
            if (desc == null) {
                module = null;
            } else {
                module = new Module(desc.getDisplayName());
                ClassLoader parentClassLoader = classLoader.getParent();
                if (parentClassLoader != null) {
                    // TODO: we should actually walk up the hierarchy until we identify a class loader
                    Module parentModule = getModule(moduleInspector, moduleMap, parentClassLoader);
                    if (parentModule != null) {
                        parentModule.addChild(module);
                    }
                }
            }
            moduleMap.put(classLoader, module);
            return module;
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!moduleInspectorFactory.isAvailable()) {
            request.setAttribute("serverContext", getServerContext());
            request.getRequestDispatcher("/WEB-INF/view/noprofile.jspx").forward(request, response);
        } else {
            List<Message> messages = new ArrayList<Message>();
            List<Module> rootModules = new ArrayList<Module>();
            ThreadLocalLogger.setTarget(messages);
            try {
                ModuleInspector moduleInspector = moduleInspectorFactory.createModuleInspector();
                
                moduleInspector.listModules();
                
                Map<ClassLoader,Module> moduleMap = new IdentityHashMap<ClassLoader,Module>();
                for (ResourceEnumeratorFactory resourceEnumeratorFactory : availableResourceEnumeratorFactories) {
                    ResourceEnumerator resourceEnumerator = resourceEnumeratorFactory.createEnumerator();
                    while (resourceEnumerator.next()) {
                        for (ClassLoader classLoader : resourceEnumerator.getClassLoaders()) {
                            if (classLoader != null) {
                                Module module = getModule(moduleInspector, moduleMap, classLoader);
                                // TODO: we should actually walk up the hierarchy until we identify a class loader (because an application may create its own class loaders)
                                if (module != null) {
                                    module.getResources().add(new Resource(resourceEnumerator.getDescription()));
                                    break;
                                }
                            }
                        }
                    }
                }
                for (Module module : moduleMap.values()) {
                    if (module != null /*&& module.getParent() == null*/) {
                        rootModules.add(module);
                    }
                }
            } finally {
                ThreadLocalLogger.setTarget(null);
            }
            Collections.sort(rootModules, new Comparator<Module>() {
                public int compare(Module o1, Module o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
            // TODO: we should also display the unavailable ResourceEnumeratorFactory instances
            request.setAttribute("factories", availableResourceEnumeratorFactories);
            request.setAttribute("messages", messages);
            request.setAttribute("rootModules", rootModules);
            request.getRequestDispatcher("/WEB-INF/view/resources.jspx").forward(request, response);
        }
    }
}
