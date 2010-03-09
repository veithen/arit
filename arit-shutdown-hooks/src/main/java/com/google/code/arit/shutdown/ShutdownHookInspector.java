package com.google.code.arit.shutdown;

import java.util.List;

public interface ShutdownHookInspector {
    List<Thread> getShutdownHooks();
}
