package com.google.code.rex.threads;

import java.util.Map;

public interface ThreadLocalInspector {
    Map<ThreadLocal<?>,Object> getThreadLocalMap(Thread thread);
}
