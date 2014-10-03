package com.googlecode.arit.threads;

import java.util.HashSet;
import java.util.Set;

import com.googlecode.arit.Formatter;
import com.googlecode.arit.resource.ClassLoaderReference;
import com.googlecode.arit.resource.Resource;
import com.googlecode.arit.resource.ResourceType;

public class ThreadResource implements Resource<Thread> {
	private final ResourceType resourceType;
	private final String description;
	private final Thread threadObject;
	private final Set<ClassLoaderReference> classLoaderReferences;

	public ThreadResource(Thread threadObject, ResourceType resourceType, String description) {
		this.resourceType = resourceType;
		this.description = description;
		this.threadObject = threadObject;
		this.classLoaderReferences = new HashSet<ClassLoaderReference>();
	}

	public final ResourceType getResourceType() {
		return resourceType;
	}

	public String getDescription(Formatter formatter) {
		return description + ": " + threadObject.getName() + " [" + threadObject.getId() + "]";
	}

	public Thread getResourceObject() {
		return threadObject;
	}

	public Set<ClassLoaderReference> getClassLoaderReferences() {
		return classLoaderReferences;
	}

	public void addReferencedClassLoaders(Set<ClassLoaderReference> classLoaderReferences) {
		this.classLoaderReferences.addAll(classLoaderReferences);
	}

	public boolean isGarbageCollectable() {
		return false;
	}
	
}