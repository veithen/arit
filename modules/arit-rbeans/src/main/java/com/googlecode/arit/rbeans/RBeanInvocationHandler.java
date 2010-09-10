package com.googlecode.arit.rbeans;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

public class RBeanInvocationHandler implements InvocationHandler {
    private final Map<Method,MethodHandler> methodHandlers;
    private final Object target;
    
    public RBeanInvocationHandler(Map<Method, MethodHandler> methodHandlers, Object target) {
        this.methodHandlers = methodHandlers;
        this.target = target;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return methodHandlers.get(method).invoke(target, args);
    }
}
