package com.google.code.rex.threads;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Exchanger;

import org.junit.Assert;
import org.junit.Test;

public class ThreadInspectorTest {
    @Test
    public void test() throws Exception {
        Timer timer = new Timer();
        // java.util.Timer doesn't offer any method to get the timer thread; thus
        // we need to get this information from within a TimerTask.
        final Exchanger<Thread> exchanger = new Exchanger<Thread>();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    exchanger.exchange(Thread.currentThread());
                } catch (InterruptedException ex) {
                    Thread.interrupted();
                }
            }
        }, 0);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                
            }
        }, 1000, 1000);
        Thread thread = exchanger.exchange(null);
        ThreadDescription description = ThreadInspectors.getInstance().getDescription(thread);
        Assert.assertTrue(description.getDescription().contains(ThreadInspectorTest.class.getName() + "$"));
        timer.cancel();
    }
}
