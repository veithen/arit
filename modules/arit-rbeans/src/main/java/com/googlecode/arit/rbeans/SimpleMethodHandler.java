package com.googlecode.arit.rbeans;

import java.lang.reflect.Method;

public class SimpleMethodHandler implements MethodHandler {
    private final Method targetMethod;
    private final ObjectHandler resultHandler;

    public SimpleMethodHandler(Method targetMethod, ObjectHandler resultHandler) {
        this.targetMethod = targetMethod;
        this.resultHandler = resultHandler;
    }

    public Object invoke(Object target, Object[] args) throws Throwable {
        return resultHandler.handle(targetMethod.invoke(target, args));
    }
}
