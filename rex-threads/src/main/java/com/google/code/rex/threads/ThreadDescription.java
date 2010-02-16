package com.google.code.rex.threads;

import java.util.Set;

public class ThreadDescription {
    private final String description;
    private final Set<ClassLoader> classLoaders;
    
    public ThreadDescription(String description, Set<ClassLoader> classLoaders) {
        this.description = description;
        this.classLoaders = classLoaders;
    }

    public String getDescription() {
        return description;
    }

    public Set<ClassLoader> getClassLoaders() {
        return classLoaders;
    }
}
