package com.googlecode.arit.rbeans;

import java.lang.reflect.Field;

public class AccessorHandler implements MethodHandler {
    private final Field field;
    
    public AccessorHandler(Field field) {
        this.field = field;
    }

    public Object invoke(Object target, Object[] args) throws Throwable {
        return field.get(target);
    }
}
