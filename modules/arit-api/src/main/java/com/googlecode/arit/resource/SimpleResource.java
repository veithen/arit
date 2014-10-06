package com.googlecode.arit.resource;

import java.util.HashSet;
import java.util.Set;

import com.googlecode.arit.Formatter;

/**
 * Simple implementation of resource type
 * 
 */
public class SimpleResource<T> implements Resource<T> {

	private final ResourceType resourceType;
	private final T resourceObject;
	private final String description;
	private final Set<ClassLoaderReference> classLoaderReferences;
	private boolean isGarbageCollectable;

	public SimpleResource(ResourceType resourceType, T resourceObject, String description) {
		this.resourceType = resourceType;
		this.resourceObject = resourceObject;
		this.description = description;
		this.classLoaderReferences = new HashSet<ClassLoaderReference>();
		this.isGarbageCollectable = false;
	}

	public ResourceType getResourceType() {
		return resourceType;
	}

	public T getResourceObject() {
		return resourceObject;
	}

	public String getDescription(Formatter formatter) {
		return description;
	}

	public boolean isGarbageCollectable() {
		return isGarbageCollectable;
	}

	public Set<ClassLoaderReference> getClassLoaderReferences() {
		return this.classLoaderReferences;
	}

	public void addClassloaderReference(ClassLoader classLoader, String refDescription) {
		classLoaderReferences.add(new SimpleClassLoaderReference(classLoader, refDescription));
	}

	public void addClassloaderReference(ClassLoaderReference clRef) {
		classLoaderReferences.add(clRef);
	}

	public void setGarbageCollectable(boolean isGarbageCollectable) {
		this.isGarbageCollectable = isGarbageCollectable;
	}

}
