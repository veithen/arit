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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class RBeanFactory {
    private final ClassLoader cl;
    // TODO: we use a LinkedHashMap to make the behavior of the factory reproducible (there seems to be a bug...)
    private final Map<Class<?>,RBeanInfo> rbeanInfoMap = new LinkedHashMap<Class<?>,RBeanInfo>();
    
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
    
    private Class<?> getTargetClass(Class<?> rbeanClass) throws RBeanFactoryException {
        Class<?> targetClass = null;
        TargetClass targetClassAnnotation = rbeanClass.getAnnotation(TargetClass.class);
        if (targetClassAnnotation != null) {
            targetClass = targetClassAnnotation.value();
        }
        Target targetAnnotation = rbeanClass.getAnnotation(Target.class);
        if (targetAnnotation != null) {
            if (targetClass != null) {
                // TODO: use exception subclass
                throw new RBeanFactoryException("Unexpected annotation @Target; already found @TargetClass");
            }
            String targetClassName = targetAnnotation.value();
            try {
                targetClass = cl.loadClass(targetClassName);
            } catch (ClassNotFoundException ex) {
                throw new TargetClassNotFoundException(ex.getMessage());
            }
        }
        if (targetClass == null) {
            // TODO: use exception subclass
            throw new RBeanFactoryException("An RBean interface must be annotated with @Target or @TargetClass");
        }
        return targetClass;
    }
    
    private void load(Class<?> rbeanClass) throws RBeanFactoryException {
        if (rbeanInfoMap.containsKey(rbeanClass)) {
            return;
        }
        boolean isStatic;
        if (RBean.class.isAssignableFrom(rbeanClass)) {
            isStatic = false;
        } else if (StaticRBean.class.isAssignableFrom(rbeanClass)) {
            isStatic = true;
        } else {
            // TODO: rename annotation
            throw new MissingRBeanAnnotationException(rbeanClass.getName() + " neither implements "
                    + RBean.class.getName() + " nor " + StaticRBean.class.getName());
        }
        Class<?> targetClass = getTargetClass(rbeanClass);
        Map<Method,MethodHandler> methodHandlers = new HashMap<Method,MethodHandler>();
        for (Method proxyMethod : rbeanClass.getMethods()) {
            boolean optional = proxyMethod.getAnnotation(Optional.class) != null;
            MethodHandler methodHandler;
            Accessor accessorAnnotation = proxyMethod.getAnnotation(Accessor.class);
            if (accessorAnnotation != null) {
                Field field = null;
                ObjectHandler valueHandler = null;
                for (String name : accessorAnnotation.name()) {
                    try {
                        field = targetClass.getDeclaredField(name);
                        valueHandler = getObjectHandler(proxyMethod.getGenericReturnType(), field.getGenericType(), proxyMethod.getAnnotation(Mapped.class) != null);
                        if (valueHandler != null) {
                            break;
                        }
                    } catch (NoSuchFieldException ex) {
                        // Continue
                    }
                }
                if (valueHandler == null) {
                    if (optional) {
                        methodHandler = NullHandler.INSTANCE;
                    } else {
                        throw new TargetMemberNotFoundException("The class " + targetClass.getClass()
                                + " doesn't contain any attribute assignment compatible with "
                                + proxyMethod.getGenericReturnType()
                                + " and with one of the following names: "
                                + Arrays.asList(accessorAnnotation.name()));
                    }
                } else {
                    field.setAccessible(true);
                    methodHandler = new AccessorHandler(field, valueHandler);
                }
            } else {
                Method targetMethod;
                try {
                    targetMethod = targetClass.getMethod(proxyMethod.getName(), proxyMethod.getParameterTypes());
                } catch (NoSuchMethodException ex) {
                    targetMethod = null;
                }
                if (targetMethod == null) {
                    if (optional) {
                        methodHandler = NullHandler.INSTANCE;
                    } else {
                        throw new TargetMemberNotFoundException("No corresponding target method found for " + proxyMethod);
                    }
                } else {
                    ObjectHandler returnHandler = getObjectHandler(proxyMethod.getGenericReturnType(), targetMethod.getGenericReturnType(), proxyMethod.getAnnotation(Mapped.class) != null);
                    if (returnHandler == null) {
                        // TODO: create new exception class
                        throw new RBeanFactoryException("The RBean method " + proxyMethod + " is not compatible with the target method "
                                + targetMethod + " because the return types are incompatible");
                    }
                    methodHandler = new SimpleMethodHandler(targetMethod, returnHandler);
                }
            }
            methodHandlers.put(proxyMethod, methodHandler);
        }
        rbeanInfoMap.put(rbeanClass, new RBeanInfo(rbeanClass, targetClass, isStatic, methodHandlers));
        SeeAlso seeAlso = rbeanClass.getAnnotation(SeeAlso.class);
        if (seeAlso != null) {
            for (Class<?> clazz : seeAlso.value()) {
                load(clazz);
            }
        }
    }
    
    private static Class<?> getRawType(Type genericType) throws RBeanFactoryException {
        if (genericType instanceof Class<?>) {
            return (Class<?>)genericType;
        } else if (genericType instanceof ParameterizedType) {
            return (Class<?>)((ParameterizedType)genericType).getRawType();
        } else if (genericType instanceof TypeVariable<?>) {
            // TODO: not entirely correct
            return Object.class;
        } else {
            throw new RBeanFactoryException("Unable to determine raw type for " + genericType);
        }
    }
    
    private ObjectHandler getObjectHandler(Type toType, Type fromType, boolean mapped) throws RBeanFactoryException {
        Class<?> toClass = getRawType(toType);
        Class<?> fromClass = getRawType(fromType);
        if (mapped || RBean.class.isAssignableFrom(toClass)) {
            if (!mapped) {
                load(toClass);
            }
            return new ObjectWrapper(this);
        } else {
            Class<?> itemClass = null;
            boolean isArray = toClass.isArray();
            if (isArray) {
                itemClass = toClass.getComponentType();
            } else if (toClass.equals(Iterable.class)) {
                itemClass = getRawType(((ParameterizedType)toType).getActualTypeArguments()[0]);
            }
            if (itemClass != null && RBean.class.isAssignableFrom(itemClass)) {
                load(itemClass);
                return new CollectionWrapper(this, itemClass, fromClass.isArray());
            } else {
                return toClass.isAssignableFrom(fromClass) ? PassThroughHandler.INSTANCE : null;
            }
        }
    }
    
    public <T> T createRBean(Class<T> rbeanClass) {
        return createRBean(rbeanClass, null);
    }
    
    // TODO: maybe we should check the runtime type of the object and return a subclass if necessary!
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
