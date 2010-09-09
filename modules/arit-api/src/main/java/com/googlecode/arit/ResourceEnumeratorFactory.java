package com.googlecode.arit;

public interface ResourceEnumeratorFactory {
    String getDescription();
    ResourceEnumerator createEnumerator();
}
