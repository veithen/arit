package com.googlecode.arit.resource;

import com.googlecode.arit.Formatter;

public class SimpleClassLoaderReference implements ClassLoaderReference {

	private final ClassLoader classLoader;
	private final String description;

	public SimpleClassLoaderReference(ClassLoader classLoader, String description) {
		this.classLoader = classLoader;
		this.description = description;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public String getDescription(Formatter formatter) {
		return description;
	}

}
