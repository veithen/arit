package com.google.code.rex;

public interface ResourceEnumerator {
    ClassLoader getClassLoader();
    String getDescription();
    boolean next();
}
