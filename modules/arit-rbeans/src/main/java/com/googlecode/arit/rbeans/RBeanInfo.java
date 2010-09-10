package com.googlecode.arit.rbeans;

import java.lang.reflect.Method;
import java.util.Map;

public class RBeanInfo {
    private final Class<?> rbeanClass;
    private final Class<?> targetClass;
    private final boolean isStatic;
    private final Map<Method,MethodHandler> methodHandlers;
    
    RBeanInfo(Class<?> rbeanClass, Class<?> targetClass, boolean isStatic, Map<Method,MethodHandler> methodHandlers) {
        this.rbeanClass = rbeanClass;
        this.targetClass = targetClass;
        this.isStatic = isStatic;
        this.methodHandlers = methodHandlers;
    }

    public Class<?> getRBeanClass() {
        return rbeanClass;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public boolean isStatic() {
        return isStatic;
    }

    Map<Method,MethodHandler> getMethodHandlers() {
        return methodHandlers;
    }
}
