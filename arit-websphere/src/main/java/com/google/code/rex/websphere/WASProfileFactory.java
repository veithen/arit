package com.google.code.rex.websphere;

import java.lang.reflect.Field;

import com.google.code.rex.ServerContext;
import com.google.code.rex.ServerProfile;
import com.google.code.rex.ServerProfileFactory;
import com.google.code.rex.util.ReflectionUtil;

public class WASProfileFactory implements ServerProfileFactory {
    public ServerProfile createServerProfile(ServerContext serverContext) {
        Class<?> classLoaderClass = serverContext.getApplicationClassLoader().getClass();
        if (classLoaderClass.getName().equals("com.ibm.ws.classloader.CompoundClassLoader")) {
            Field nameField;
            try {
                nameField = ReflectionUtil.getField(classLoaderClass, "name");
            } catch (NoSuchFieldException ex) {
                return null;
            }
            return new WASProfile(classLoaderClass, nameField);
        } else {
            return null;
        }
    }
}
