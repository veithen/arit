package com.googlecode.arit.rbeans;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public class RBeanFactory<T> {
    private final Class<T> rbeanClass;
    private final Class<?> targetClass;
    private final Map<Method,MethodHandler> methodHandlers;
    
    public RBeanFactory(Class<T> rbeanClass, Class<?> targetClass, Map<Method,MethodHandler> methodHandlers) {
        this.rbeanClass = rbeanClass;
        this.targetClass = targetClass;
        this.methodHandlers = methodHandlers;
    }

    public T createRBean(Object object) {
        return rbeanClass.cast(Proxy.newProxyInstance(RBeanFactory.class.getClassLoader(),
                new Class<?>[] { rbeanClass }, new RBeanInvocationHandler(methodHandlers, object)));
    }
    
    public boolean appliesTo(Object object) {
        return targetClass.isInstance(object);
    }
    
    public static <T> RBeanFactory<T> create(Class<T> rbeanClass) throws RBeanFactoryException {
        return create(rbeanClass, RBeanFactory.class.getClassLoader());
    }
    
    public static <T> RBeanFactory<T> create(Class<T> rbeanClass, ClassLoader cl) throws RBeanFactoryException {
        return new RBeanFactoryBuilder(cl).getFactory(rbeanClass);
    }
}
