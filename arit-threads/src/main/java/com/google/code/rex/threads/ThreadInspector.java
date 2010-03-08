package com.google.code.rex.threads;

public interface ThreadInspector {
    /**
     * Get a description of the given thread.
     * 
     * @param thread
     *            the thread to inspect
     * @return a description of the thread, or <code>null</code> if the inspector doesn't recognize
     *         the given type of thread
     */
    ThreadDescription getDescription(Thread thread);
    
    int getPriority();
}
