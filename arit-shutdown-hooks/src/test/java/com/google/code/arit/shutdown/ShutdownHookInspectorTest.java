package com.google.code.arit.shutdown;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.google.code.arit.ProviderFinder;

public class ShutdownHookInspectorTest {
    @Test
    public void test() {
        List<ShutdownHookInspector> inspectors = ProviderFinder.find(ShutdownHookInspector.class);
        Assert.assertEquals(1, inspectors.size());
        ShutdownHookInspector inspector = inspectors.get(0);
        Thread hook = new Thread();
        Runtime runtime = Runtime.getRuntime();
        runtime.addShutdownHook(hook);
        List<Thread> hooks = inspector.getShutdownHooks();
        Assert.assertTrue(hooks.contains(hook));
        runtime.removeShutdownHook(hook);
    }
}
