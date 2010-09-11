package com.googlecode.arit.rbeans;

/**
 * Indicates that the class specified by {@link RBean#target()} was not found.
 * 
 * @author Andreas Veithen
 */
public class TargetClassNotFoundException extends RBeanFactoryException {
    private static final long serialVersionUID = -3879998160549036596L;

    public TargetClassNotFoundException(String message) {
        super(message);
    }
}
