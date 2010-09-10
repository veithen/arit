package com.googlecode.arit.rbeans;

public class PassThroughHandler implements ObjectHandler {
    public static final PassThroughHandler INSTANCE = new PassThroughHandler();

    public Object handle(Object object) {
        return object;
    }
}
