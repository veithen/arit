package com.googlecode.arit.rbeans;

public interface MethodHandler {
    Object invoke(Object target, Object[] args) throws Throwable;
}
