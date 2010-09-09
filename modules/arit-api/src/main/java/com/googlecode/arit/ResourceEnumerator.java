package com.googlecode.arit;

import java.util.Collection;

public interface ResourceEnumerator {
    Collection<ClassLoader> getClassLoaders();
    String getDescription();
    boolean next();
}
