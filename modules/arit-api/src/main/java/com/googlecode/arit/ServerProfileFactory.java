package com.googlecode.arit;

/**
 * Detects a particular class of application servers and creates a {@link ServerProfile}.
 * 
 * @author Andreas Veithen
 */
public interface ServerProfileFactory {
    /**
     * Create a server profile.
     * 
     * @param serverContext
     *            provides information about the server
     * @return a server profile, or <code>null</code> if the server is not supported by this factory
     */
    ServerProfile createServerProfile(ServerContext serverContext);
}
