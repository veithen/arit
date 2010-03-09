package com.google.code.arit.shutdown;

import com.google.code.arit.ResourceEnumerator;
import com.google.code.arit.ResourceEnumeratorFactory;

public class ShutdownHookEnumeratorFactory implements ResourceEnumeratorFactory {
    private final ShutdownHookInspector inspector;

    public ShutdownHookEnumeratorFactory(ShutdownHookInspector inspector) {
        this.inspector = inspector;
    }

    public ResourceEnumerator createEnumerator() {
        return new ShutdownHookEnumerator(inspector.getShutdownHooks());
    }
}
