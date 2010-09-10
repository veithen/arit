package com.googlecode.arit.rbeans;

import java.lang.reflect.Field;

public class AccessorHandler implements MethodHandler {
    private final Field field;
    private final ObjectHandler valueHandler;
    
    public AccessorHandler(Field field, ObjectHandler valueHandler) {
        this.field = field;
        this.valueHandler = valueHandler;
    }

    public Object invoke(Object target, Object[] args) throws Throwable {
        return valueHandler.handle(field.get(target));
    }
}
