package com.googlecode.arit.threads;

import java.util.Map;

public interface ThreadLocalInspector {
    Map<ThreadLocal<?>,Object> getThreadLocalMap(Thread thread);
}
