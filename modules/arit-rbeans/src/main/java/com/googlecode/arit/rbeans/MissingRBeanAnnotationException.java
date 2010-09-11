package com.googlecode.arit.rbeans;

public class MissingRBeanAnnotationException extends RBeanFactoryException {
    private static final long serialVersionUID = 1328069766809958329L;

    public MissingRBeanAnnotationException(String message) {
        super(message);
    }
}
