package com.googlecode.arit.rbeans;

import java.lang.reflect.Method;

public class SimpleMethodHandler implements MethodHandler {
    private final Method targetMethod;

    public SimpleMethodHandler(Method targetMethod) {
        this.targetMethod = targetMethod;
    }

    public Object invoke(Object target, Object[] args) throws Throwable {
        return targetMethod.invoke(target, args);
    }
}
