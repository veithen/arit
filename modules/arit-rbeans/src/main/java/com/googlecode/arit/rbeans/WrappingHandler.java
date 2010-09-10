package com.googlecode.arit.rbeans;

public class WrappingHandler implements ObjectHandler {
    private final RBeanFactory rbf;

    public WrappingHandler(RBeanFactory rbf) {
        this.rbf = rbf;
    }

    public Object handle(Object object) {
        return object == null ? null : rbf.createRBean(rbf.getRBeanInfoForTargetClass(object.getClass()), object);
    }
}
