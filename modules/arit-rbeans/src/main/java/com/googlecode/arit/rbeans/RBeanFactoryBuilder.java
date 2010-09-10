package com.googlecode.arit.rbeans;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class RBeanFactoryBuilder {
    private final ClassLoader cl;
    private final Map<Class<?>,RBeanFactory<?>> factories = new HashMap<Class<?>,RBeanFactory<?>>();

    public RBeanFactoryBuilder(ClassLoader cl) {
        this.cl = cl;
    }
    
    public <T> RBeanFactory<T> getFactory(Class<T> rbeanClass) throws RBeanFactoryException {
        RBeanFactory<?> factory = factories.get(rbeanClass);
        if (factory == null) {
            factory = createFactory(rbeanClass);
            factories.put(rbeanClass, factory);
        }
        return (RBeanFactory<T>)factory;
    }
    
    private <T> RBeanFactory<T> createFactory(Class<T> rbeanClass) throws RBeanFactoryException {
        RBean rbeanAnnotation = rbeanClass.getAnnotation(RBean.class);
        if (rbeanAnnotation == null) {
            throw new RBeanFactoryException("No RBean annotation found on class " + rbeanClass.getName());
        }
        String targetClassName = rbeanAnnotation.targetClass();
        Class<?> targetClass;
        try {
            targetClass = cl.loadClass(targetClassName);
        } catch (ClassNotFoundException ex) {
            throw new RBeanFactoryException(ex);
        }
        Map<Method,MethodHandler> methodHandlers = new HashMap<Method,MethodHandler>();
        for (Method proxyMethod : rbeanClass.getMethods()) {
            MethodHandler methodHandler;
            Accessor accessorAnnotation = proxyMethod.getAnnotation(Accessor.class);
            if (accessorAnnotation != null) {
                NoSuchFieldException exception = null;
                Field field = null;
                for (String name : accessorAnnotation.name()) {
                    try {
                        field = targetClass.getDeclaredField(name);
                        field.setAccessible(true);
                        break;
                    } catch (NoSuchFieldException ex) {
                        if (exception == null) {
                            exception = ex;
                        }
                    }
                }
                if (field == null) {
                    throw new RBeanFactoryException(exception);
                }
                methodHandler = new AccessorHandler(field);
            } else {
                try {
                    methodHandler = new SimpleMethodHandler(targetClass.getMethod(proxyMethod.getName(), proxyMethod.getParameterTypes()));
                } catch (NoSuchMethodException ex) {
                    throw new RBeanFactoryException(ex);
                }
            }
            methodHandlers.put(proxyMethod, methodHandler);
        }
        return new RBeanFactory<T>(rbeanClass, targetClass, methodHandlers);
    }
}
