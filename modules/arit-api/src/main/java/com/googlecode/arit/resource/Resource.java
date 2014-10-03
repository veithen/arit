package com.googlecode.arit.resource;

import java.util.Set;

import com.googlecode.arit.Formatter;

/**
 * A resource which can be scanned and listed by arit.
 *
 * @param <T>
 *            The type of the resource object
 */
public interface Resource<T> {
	/**
	 * 
	 * @return the type of this resource
	 */
	ResourceType getResourceType();

	/**
	 * Get the object that represents this resource. The returned object may be of any type. The only constraint is that
	 * the same instance must be returned when a resources is visited twice.
	 * 
	 * @return the object representing this resource; must not be <code>null</code>
	 */
	T getResourceObject();

	/**
	 * Get a (human readable) description of the current resource.
	 * 
	 * @param formatter
	 *            TODO
	 * 
	 * @return the description of the current resource
	 */
	String getDescription(Formatter formatter);

	/**
	 * 
	 * @return Whether this resource is maintained by a weak/soft/phantom reference, and thus still can be garbage
	 *         collected. This is typically the case for caches.
	 */
	boolean isGarbageCollectable();

	Set<ClassLoaderReference> getClassLoaderReferences();

}
