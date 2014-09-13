package com.googlecode.arit.resource;

import com.googlecode.arit.Formatter;

/**
 * A reference to a class loader from a resource
 */
public interface ClassLoaderReference {
	public ClassLoader getClassLoader();

	public String getDescription(Formatter formatter);
}
