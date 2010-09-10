package com.googlecode.arit.rbeans;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class RBeanFactory {
    private final ClassLoader cl;
    private final Map<Class<?>,RBeanInfo> rbeanInfoMap = new HashMap<Class<?>,RBeanInfo>();
    
    public RBeanFactory() {
        this(RBeanFactory.class.getClassLoader());
    }
    
    public RBeanFactory(ClassLoader cl) {
        this.cl = cl;
    }

    public synchronized RBeanInfo getRBeanInfo(Class<?> rbeanClass) throws RBeanFactoryException {
        RBeanInfo rbeanInfo = rbeanInfoMap.get(rbeanClass);
        if (rbeanInfo == null) {
            rbeanInfo = createRBeanInfo(rbeanClass);
            rbeanInfoMap.put(rbeanClass, rbeanInfo);
        }
        return rbeanInfo;
    }
    
    synchronized RBeanInfo getRBeanInfoForTargetClass(Class<?> targetClass) {
        RBeanInfo result = null;
        for (RBeanInfo rbeanInfo : rbeanInfoMap.values()) {
            Class<?> rbeanTargetClass = rbeanInfo.getTargetClass();
            if (rbeanTargetClass.isAssignableFrom(targetClass)
                    && (result == null || result.getTargetClass().isAssignableFrom(targetClass))) {
                result = rbeanInfo;
            }
        }
        return result;
    }
    
    public boolean check(Class<?>... rbeanClasses) {
        try {
            for (Class<?> rbeanClass : rbeanClasses) {
                getRBeanInfo(rbeanClass);
            }
            return true;
        } catch (RBeanFactoryException ex) {
            return false;
        }
    }
    
    private RBeanInfo createRBeanInfo(Class<?> rbeanClass) throws RBeanFactoryException {
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
                Method targetMethod;
                try {
                    targetMethod = targetClass.getMethod(proxyMethod.getName(), proxyMethod.getParameterTypes());
                } catch (NoSuchMethodException ex) {
                    throw new RBeanFactoryException(ex);
                }
                methodHandler = new SimpleMethodHandler(targetMethod, getResultHandler(proxyMethod.getReturnType(), targetMethod.getReturnType()));
            }
            methodHandlers.put(proxyMethod, methodHandler);
        }
        return new RBeanInfo(rbeanClass, targetClass, methodHandlers);
    }
    
    private ObjectHandler getResultHandler(Class<?> proxyReturnType, Class<?> targetReturnType) {
        if (proxyReturnType.getAnnotation(RBean.class) == null) {
            return PassThroughHandler.INSTANCE;
        } else {
            // Ensure that the RBeanInfo is loaded so that we can later locate it based on
            // the target class
            getRBeanInfo(proxyReturnType);
            return new WrappingHandler(this);
        }
    }
    
    public <T> T createRBean(Class<T> rbeanClass, Object object) throws RBeanFactoryException {
        return rbeanClass.cast(createRBean(getRBeanInfo(rbeanClass), object));
    }
    
    Object createRBean(RBeanInfo rbeanInfo, Object object) throws RBeanFactoryException {
        return Proxy.newProxyInstance(RBeanFactory.class.getClassLoader(),
                new Class<?>[] { rbeanInfo.getRBeanClass() }, new RBeanInvocationHandler(rbeanInfo.getMethodHandlers(), object));
    }
}
