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
package com.googlecode.arit.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.googlecode.arit.Logger;
import com.googlecode.arit.module.ModuleDescription;
import com.googlecode.arit.module.ModuleInspector;
import com.googlecode.arit.module.ModuleType;
import com.googlecode.arit.resource.ClassLoaderReference;
import com.googlecode.arit.resource.Resource;
import com.googlecode.arit.resource.ResourceEnumerator;
import com.googlecode.arit.resource.ResourceEnumeratorFactory;
import com.googlecode.arit.resource.ResourceScanner;
import com.googlecode.arit.resource.ResourceScanner.ResourceListener;
import com.googlecode.arit.resource.ResourceScannerFacet;
import com.googlecode.arit.resource.ResourceType;
import com.googlecode.arit.servlet.ModuleInspectorFactory;
import com.googlecode.arit.servlet.ModuleTypeIconManager;
import com.googlecode.arit.servlet.ResourceTypeIconManager;
import com.googlecode.arit.servlet.log.SimpleLogger;

@ManagedResource(objectName="com.googlecode.arit:type=ReportGenerator", description="Generates Arit reports")
public class ReportGenerator implements InitializingBean, DisposableBean {
    private static final Log log = LogFactory.getLog(ReportGenerator.class);
    
    @Autowired
    private ModuleInspectorFactory moduleInspectorFactory;
    
    @Autowired
	private Set<ResourceEnumeratorFactory<?>> oldResourceEnumeratorFactories;

	@Autowired
	private Set<ResourceScanner> resourceEnumerators;
    
    @Autowired
    private ClassLoaderIdProvider classLoaderIdProvider;
    
    @Autowired
    private ResourceIdProvider resourceIdProvider;
    
    @Autowired
    @Qualifier("unknown")
    private ModuleType unknownModuleType;
    
    @Autowired
    private ModuleTypeIconManager moduleTypeIconManager;
    
//    @Requirement(role=IconManager.class, hint="identity")
//    private ModuleTypeIconManager moduleIdentityTypeIconManager;
    
    @Autowired
    private ModuleIdentityProvider moduleIdentityProvider;
    
    @Autowired
    private ResourceTypeIconManager resourceTypeIconManager;
    
    private List<ResourceEnumeratorFactory<?>> availableOldResourceEnumeratorFactories = new ArrayList<ResourceEnumeratorFactory<?>>();
    private List<ResourceEnumeratorFactory<?>> unavailableOldResourceEnumeratorFactories = new ArrayList<ResourceEnumeratorFactory<?>>();
    
	private List<ResourceScanner> availableResourceScanners = new ArrayList<ResourceScanner>();
	private List<ResourceScanner> unavailableResourceEnumerators = new ArrayList<ResourceScanner>();

    
    public void afterPropertiesSet() throws Exception {
		for (ResourceEnumeratorFactory<?> resourceEnumeratorFactory : oldResourceEnumeratorFactories) {
            if (resourceEnumeratorFactory.isAvailable()) {
                availableOldResourceEnumeratorFactories.add(resourceEnumeratorFactory);
            } else {
                unavailableOldResourceEnumeratorFactories.add(resourceEnumeratorFactory);
            }
        }

		for (ResourceScanner resourceScanner : resourceEnumerators) {
			if (resourceScanner.isAvailable()) {
				availableResourceScanners.add(resourceScanner);
			} else {
				unavailableResourceEnumerators.add(resourceScanner);
			}
		}
    }
    
    public void destroy() throws Exception {
        availableOldResourceEnumeratorFactories.clear();
        unavailableOldResourceEnumeratorFactories.clear();

		availableResourceScanners.clear();
		unavailableResourceEnumerators.clear();
    }

    public boolean isAvailable() {
        return moduleInspectorFactory.isAvailable();
    }
    
    @ManagedOperation(description="Generate an Arit report")
    public Report generateReport() {
        return generateReport(false);
    }
    
    public Report generateReport(boolean leaksOnly) {
        int failures = 0;
        while (true) {
            try {
                return internalGenerateReport(leaksOnly);
            } catch (ConcurrentModificationException ex) {
                if (++failures == 3) {
                    throw ex;
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("Attempt " + failures + " to generate a report failed, keep trying", ex);
                    }
                }
            }
        }
    }
    
    private Report internalGenerateReport(boolean leaksOnly) {
        List<Message> messages = new ArrayList<Message>();
        Logger logger = new SimpleLogger(messages);
        ModuleInspector moduleInspector = moduleInspectorFactory.createModuleInspector();
		final ModuleHelper moduleHelper =
				new ModuleHelper(moduleInspector, classLoaderIdProvider, unknownModuleType, moduleTypeIconManager,
						moduleIdentityProvider);
        
        // If the application server supports this, load the entire module list before starting
        // to inspect the resources.
        List<ModuleDescription> moduleDescriptions = moduleInspector.listModules();
        if (moduleDescriptions != null) {
            for (ModuleDescription desc : moduleDescriptions) {
                moduleHelper.getModule(desc);
            }
        }

		// fetch all resources for the old type resource enumerators...
        for (ResourceEnumeratorFactory<?> resourceEnumeratorFactory : availableOldResourceEnumeratorFactories) {
            ResourceEnumerator resourceEnumerator = resourceEnumeratorFactory.createEnumerator(logger);
            while (resourceEnumerator.nextResource()) {
				linkResourceToModules(moduleHelper, resourceEnumerator);
            }
        }
        //and the new type...
		for (ResourceScanner resourceScanner : availableResourceScanners) {
            resourceScanner.scanForResources(new ResourceListener() {
				public void onResourceFound(Resource<?> resource) {
					linkResourceToModules(moduleHelper, resource);
				}
			});
        }
        

		List<Module> rootModules = getSortedRootModules(moduleHelper.getModules(), leaksOnly);
        return new Report(messages, rootModules);
    }

	private List<Module> getSortedRootModules(Set<Module> modules, boolean leaksOnly) {
		List<Module> rootModules = new ArrayList<Module>();
		for (Module module : modules) {
            if (module != null && module.getParent() == null && (!leaksOnly || module.isStopped())) {
                rootModules.add(module);
            }
        }
        Collections.sort(rootModules, new Comparator<Module>() {
            public int compare(Module o1, Module o2) {
                String name1 = o1.getName();
                String name2 = o2.getName();
                if (name1 == null && name2 == null) {
                    return 0;
                } else if (name1 == null) {
                    return -1;
                } else if (name2 == null) {
                    return 1;
                } else {
                    int c = name1.compareTo(name2);
                    return c != 0 ? c : o1.getId().compareTo(o2.getId());
                }
            }
        });
		return rootModules;
	}

	private void linkResourceToModules(ModuleHelper moduleHelper, Resource<?> resource) {
		// A resource has class loader references to multiple class loaders and therefore to multiple
		// modules. In this case the report contains several Resource instances for a single resource.
		// This map is used to keep track of these instances.
		Map<Module, ResourcePresentation> resourceMap = new HashMap<Module, ResourcePresentation>();
		String resourceDescription = null;
		Integer resourceId = null;
		Set<ClassLoaderReference> classLoaderReferences = resource.getClassLoaderReferences();
		List<ClassLoaderReference> sortedClassLoaderReferences = new ArrayList<ClassLoaderReference>(classLoaderReferences);
		Collections.sort(sortedClassLoaderReferences, new Comparator<ClassLoaderReference>() {

			public int compare(ClassLoaderReference o1, ClassLoaderReference o2) {
				//
				return 0;
			}

		});
		
		for (ClassLoaderReference classLoaderReference : classLoaderReferences) {
			ClassLoader classLoader = classLoaderReference.getClassLoader();
			if (classLoader != null) { // TODO: do we really need this check??
				ModuleInfo moduleInfo = moduleHelper.getModule(classLoader);
				if (moduleInfo != null) {
					Module module = moduleInfo.getModule();
					ResourcePresentation resourcePresentation = resourceMap.get(module);
					if (resourcePresentation == null) {
						ResourceType resourceType = resource.getResourceType();
						if (resourceId == null) {
							resourceId =
									resourceIdProvider.getResourceId(resourceType.getIdentifier(),
											resource.getResourceObject(), true);
						}
						if (resourceDescription == null) {
							resourceDescription = resource.getDescription(moduleInfo);
							if (resourceType.isShowResourceId()) {
								resourceDescription = resourceDescription + " (" + resourceId + ")";
							}
						}
						resourcePresentation =
								new ResourcePresentation(resourceId, resourceTypeIconManager.getIcon(resourceType)
										.getIconImage("default").getFileName(), resourceType.getIdentifier(),
										resourceDescription);
						module.getResources().add(resourcePresentation);
						resourceMap.put(module, resourcePresentation);
					}
					resourcePresentation.getLinks().add(
							new ClassLoaderLink(classLoaderReference.getDescription(moduleInfo)));
				}
			}
		}
	}

	private void linkResourceToModules(ModuleHelper moduleHelper, ResourceEnumerator resourceEnumerator) {
		// A resource has class loader references to multiple class loaders and therefore to multiple
		// modules. In this case the report contains several Resource instances for a single resource.
		// This map is used to keep track of these instances.
		Map<Module, ResourcePresentation> resourceMap = new HashMap<Module, ResourcePresentation>();
		String resourceDescription = null;
		Integer resourceId = null;
		while (resourceEnumerator.nextClassLoaderReference()) {
		    ClassLoader classLoader = resourceEnumerator.getReferencedClassLoader();
		    if (classLoader != null) { // TODO: do we really need this check??
		        ModuleInfo moduleInfo = moduleHelper.getModule(classLoader);
		        if (moduleInfo != null) {
		            Module module = moduleInfo.getModule();
					ResourcePresentation resource = resourceMap.get(module);
		            if (resource == null) {
		                ResourceType resourceType = resourceEnumerator.getResourceType();
		                if (resourceId == null) {
		                    resourceId = resourceIdProvider.getResourceId(resourceType.getIdentifier(), resourceEnumerator.getResourceObject(), true);
		                }
		                if (resourceDescription == null) {
		                    resourceDescription = resourceEnumerator.getResourceDescription(moduleInfo);
		                    if (resourceType.isShowResourceId()) {
		                        resourceDescription = resourceDescription + " (" + resourceId + ")";
		                    }
		                }
		                resource = new ResourcePresentation(resourceId, resourceTypeIconManager.getIcon(resourceType).getIconImage("default").getFileName(), resourceType.getIdentifier(), resourceDescription);
		                module.getResources().add(resource);
		                resourceMap.put(module, resource);
		            }
		            resource.getLinks().add(new ClassLoaderLink(resourceEnumerator.getClassLoaderReferenceDescription(moduleInfo)));
		        }
		    }
		}
	}

	public List<ResourceScannerFacet> getAvailableResourceEnumeratorFactories() {
		List<ResourceScannerFacet> availableResourceEnumeratorFactories =
				new ArrayList<ResourceScannerFacet>();
		availableResourceEnumeratorFactories.addAll(this.availableResourceScanners);
		availableResourceEnumeratorFactories.addAll(this.availableOldResourceEnumeratorFactories);
		return Collections.unmodifiableList(availableResourceEnumeratorFactories);
    }
}
