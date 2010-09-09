package com.googlecode.arit.shutdown;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.googlecode.arit.ProviderFinder;
import com.googlecode.arit.shutdown.ShutdownHookInspector;

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
