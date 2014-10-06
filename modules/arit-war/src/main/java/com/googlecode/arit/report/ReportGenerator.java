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

import com.googlecode.arit.module.ModuleDescription;
import com.googlecode.arit.module.ModuleInspector;
import com.googlecode.arit.module.ModuleType;
import com.googlecode.arit.resource.ClassLoaderReference;
import com.googlecode.arit.resource.Resource;
import com.googlecode.arit.resource.ResourceScanner;
import com.googlecode.arit.resource.ResourceScanner.ResourceListener;
import com.googlecode.arit.resource.ResourceType;
import com.googlecode.arit.servlet.ModuleInspectorFactory;
import com.googlecode.arit.servlet.ModuleTypeIconManager;
import com.googlecode.arit.servlet.ResourceTypeIconManager;
import com.googlecode.arit.servlet.log.MessagesImpl;

@ManagedResource(objectName="com.googlecode.arit:type=ReportGenerator", description="Generates Arit reports")
public class ReportGenerator implements InitializingBean, DisposableBean {
    private static final Log log = LogFactory.getLog(ReportGenerator.class);
    
    @Autowired
    private ModuleInspectorFactory moduleInspectorFactory;
    
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
    
	@Autowired
	private MessagesImpl messages;

	@Autowired
	private ResourceScanningConfigImpl resourceScanningConfigImpl;

	private List<ResourceScanner> availableResourceScanners = new ArrayList<ResourceScanner>();
	private List<ResourceScanner> unavailableResourceEnumerators = new ArrayList<ResourceScanner>();

    
    public void afterPropertiesSet() throws Exception {
		for (ResourceScanner resourceScanner : resourceEnumerators) {
			if (resourceScanner.isAvailable()) {
				availableResourceScanners.add(resourceScanner);
			} else {
				unavailableResourceEnumerators.add(resourceScanner);
			}
		}
    }
    
    public void destroy() throws Exception {
		availableResourceScanners.clear();
		unavailableResourceEnumerators.clear();
    }

    public boolean isAvailable() {
        return moduleInspectorFactory.isAvailable();
    }

    @ManagedOperation(description="Generate an Arit report")
    public Report generateReport() {
        return generateReport(false, true);
    }

	public Report generateReport(boolean leaksOnly, boolean includeGCables) {
        int failures = 0;
        while (true) {
            try {
				return internalGenerateReport(leaksOnly, includeGCables);
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
    
	private Report internalGenerateReport(boolean leaksOnly, boolean includeGCables) {
		messages.reset();
		resourceScanningConfigImpl.setIncludeGarbageCollectableResources(includeGCables);

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

		// fetch all resources
		for (ResourceScanner resourceScanner : availableResourceScanners) {
            resourceScanner.scanForResources(new ResourceListener() {
				public void onResourceFound(Resource<?> resource) {
					linkResourceToModules(moduleHelper, resource);
				}
			});
        }
        

		List<Module> rootModules = getSortedRootModules(moduleHelper.getModules(), leaksOnly);
		return new Report(messages.getMessages(), rootModules);
    }

	private List<Module> getSortedRootModules(Set<Module> modules, boolean leaksOnly) {
		List<Module> rootModules = new ArrayList<Module>();
		for (Module module : modules) {
            if (module != null && module.getParent() == null && (!leaksOnly || module.isStopped())) {
                rootModules.add(module);
            }
        }
		
		Comparator<Module> moduleComparator = new Comparator<Module>() {
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
		};

		Comparator<ClassLoaderLink> classloaderLinkComparator = new Comparator<ClassLoaderLink>() {
			public int compare(ClassLoaderLink o1, ClassLoaderLink o2) {
				String name1 = o1.getDescription();
				String name2 = o2.getDescription();
				if (name1 == null && name2 == null) {
					return 0;
				} else if (name1 == null) {
					return -1;
				} else if (name2 == null) {
					return 1;
				} else {
					return name1.compareTo(name2);
				}
			}
		};

		// sort root modules on name
		Collections.sort(rootModules, moduleComparator);
        
		// sort child module and class loader relations on name
		for (Module module : modules) {
			Collections.sort(module.getChildren(), moduleComparator);

			// note: the resources' ordering remains between report generations, because the current program logic retrieves
			// them in a fixed order. Ordering resources can become necessary if this changes in the future.

			for (ResourcePresentation rp : module.getResources()) {
				Collections.sort(rp.getLinks(), classloaderLinkComparator);
			}
		}
        
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

	public List<ResourceScanner> getAvailableResourceScanners() {
		return Collections.unmodifiableList(this.availableResourceScanners);
    }
}
