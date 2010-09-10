package com.googlecode.arit.rbeans;

public class RBeanFactoryException extends RuntimeException {
    private static final long serialVersionUID = -7358570552567886100L;

    public RBeanFactoryException() {
        super();
    }

    public RBeanFactoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public RBeanFactoryException(String message) {
        super(message);
    }

    public RBeanFactoryException(Throwable cause) {
        super(cause);
    }
}
