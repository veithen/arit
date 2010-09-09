package com.googlecode.arit;

public class ProviderFinderException extends RuntimeException {
    private static final long serialVersionUID = 3670591986040636952L;

    public ProviderFinderException() {
    }

    public ProviderFinderException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProviderFinderException(String message) {
        super(message);
    }

    public ProviderFinderException(Throwable cause) {
        super(cause);
    }
}
