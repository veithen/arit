package com.googlecode.arit.shutdown;

import com.googlecode.arit.ResourceEnumerator;
import com.googlecode.arit.ResourceEnumeratorFactory;

public class ShutdownHookEnumeratorFactory implements ResourceEnumeratorFactory {
    private final ShutdownHookInspector inspector;

    public ShutdownHookEnumeratorFactory(ShutdownHookInspector inspector) {
        this.inspector = inspector;
    }

    public String getDescription() {
        return "Shutdown hooks";
    }

    public ResourceEnumerator createEnumerator() {
        return new ShutdownHookEnumerator(inspector.getShutdownHooks());
    }
}
