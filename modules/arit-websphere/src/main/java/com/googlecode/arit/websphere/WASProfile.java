package com.googlecode.arit.websphere;

import java.lang.reflect.Field;

import com.googlecode.arit.ServerProfile;

public class WASProfile implements ServerProfile {
    private final Class<?> compoundClassLoaderClass;
    private final Field nameField;
    
    public WASProfile(Class<?> compoundClassLoaderClass, Field nameField) {
        this.compoundClassLoaderClass = compoundClassLoaderClass;
        this.nameField = nameField;
    }

    public String identifyApplication(ClassLoader classLoader) {
        if (compoundClassLoaderClass.isAssignableFrom(classLoader.getClass())) {
            try {
                return (String)nameField.get(classLoader);
            } catch (IllegalAccessException ex) {
                throw new IllegalAccessError(ex.getMessage());
            }
        } else {
            return null;
        }
    }
}
