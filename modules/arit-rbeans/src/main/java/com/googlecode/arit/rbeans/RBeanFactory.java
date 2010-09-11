/*
 * Copyright 2010 Andreas Veithen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.arit.rbeans;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RBeanFactory {
    private final ClassLoader cl;
    private final Map<Class<?>,RBeanInfo> rbeanInfoMap = new HashMap<Class<?>,RBeanInfo>();
    
    public RBeanFactory(Class<?>... rbeanClasses) throws RBeanFactoryException {
        this(RBeanFactory.class.getClassLoader(), rbeanClasses);
    }
    
    public RBeanFactory(ClassLoader cl, Class<?>... rbeanClasses) throws RBeanFactoryException {
        this.cl = cl;
        for (Class<?> rbeanClass : rbeanClasses) {
            load(rbeanClass);
        }
    }
    
    public RBeanInfo getRBeanInfo(Class<?> rbeanClass) {
        RBeanInfo rbeanInfo = rbeanInfoMap.get(rbeanClass);
        if (rbeanInfo == null) {
            throw new IllegalArgumentException(rbeanClass + " is not part of this factory");
        }
        return rbeanInfo;
    }
    
    RBeanInfo getRBeanInfoForTargetClass(Class<?> targetClass) {
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
    
    private void load(Class<?> rbeanClass) throws RBeanFactoryException {
        if (rbeanInfoMap.containsKey(rbeanClass)) {
            return;
        }
        RBean rbeanAnnotation = rbeanClass.getAnnotation(RBean.class);
        if (rbeanAnnotation == null) {
            throw new MissingRBeanAnnotationException("No RBean annotation found on class " + rbeanClass.getName());
        }
        String targetClassName = rbeanAnnotation.target();
        Class<?> targetClass;
        try {
            targetClass = cl.loadClass(targetClassName);
        } catch (ClassNotFoundException ex) {
            throw new TargetClassNotFoundException(ex.getMessage());
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
                        // Continue
                    }
                }
                if (field == null) {
                    throw new TargetMemberNotFoundException("The class " + targetClass.getClass()
                            + " doesn't contain any attribute with one of the following names: "
                            + Arrays.asList(accessorAnnotation.name()));
                }
                methodHandler = new AccessorHandler(field, getResultHandler(proxyMethod.getReturnType(), field.getType()));
            } else {
                Method targetMethod;
                try {
                    targetMethod = targetClass.getMethod(proxyMethod.getName(), proxyMethod.getParameterTypes());
                } catch (NoSuchMethodException ex) {
                    throw new TargetMemberNotFoundException(ex.getMessage());
                }
                methodHandler = new SimpleMethodHandler(targetMethod, getResultHandler(proxyMethod.getReturnType(), targetMethod.getReturnType()));
            }
            methodHandlers.put(proxyMethod, methodHandler);
        }
        rbeanInfoMap.put(rbeanClass, new RBeanInfo(rbeanClass, targetClass, rbeanAnnotation.isStatic(), methodHandlers));
        SeeAlso seeAlso = rbeanClass.getAnnotation(SeeAlso.class);
        if (seeAlso != null) {
            for (Class<?> clazz : seeAlso.value()) {
                load(clazz);
            }
        }
    }
    
    private ObjectHandler getResultHandler(Class<?> proxyReturnType, Class<?> targetReturnType) throws RBeanFactoryException {
        if (proxyReturnType.getAnnotation(RBean.class) == null) {
            return PassThroughHandler.INSTANCE;
        } else {
            load(proxyReturnType);
            return new WrappingHandler(this);
        }
    }
    
    public <T> T createRBean(Class<T> rbeanClass) {
        return createRBean(rbeanClass, null);
    }
    
    public <T> T createRBean(Class<T> rbeanClass, Object object) {
        return rbeanClass.cast(createRBean(getRBeanInfo(rbeanClass), object));
    }
    
    Object createRBean(RBeanInfo rbeanInfo, Object object) {
        if (rbeanInfo.isStatic()) {
            if (object != null) {
                throw new IllegalArgumentException("No object expected for static RBean " + rbeanInfo.getRBeanClass().getName());
            }
        } else {
            if (object == null) {
                throw new IllegalArgumentException("Object must not be null because " + rbeanInfo.getRBeanClass().getName() + " is not static");
            }
        }
        return Proxy.newProxyInstance(RBeanFactory.class.getClassLoader(),
                new Class<?>[] { rbeanInfo.getRBeanClass() }, new RBeanInvocationHandler(rbeanInfo.getMethodHandlers(), object));
    }
}
