package com.google.code.arit;

public interface ResourceEnumeratorFactory {
    String getDescription();
    ResourceEnumerator createEnumerator();
}
