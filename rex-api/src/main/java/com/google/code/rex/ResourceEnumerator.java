package com.google.code.rex;

import java.util.Collection;

public interface ResourceEnumerator {
    Collection<ClassLoader> getClassLoaders();
    String getDescription();
    boolean next();
}
