package inspector;

public interface ServerProfile {
    /**
     * Determine the application to which a given class loader belongs. The implementation should
     * only inspect the given class loader, but not its parents.
     * 
     * @param classLoader
     *            the class loader to inspect
     * @return An identifier for the application, or <code>null</code> if the class loader doesn't
     *         belong to any application. The identifier should be unique, but this is not a strict
     *         requirement. The identifier must be human readable and allow the user to identify the
     *         application. It may be a context path, the name of the application, the location of
     *         the WAR or EAR file, or any other useful identifier.
     */
    String identifyApplication(ClassLoader classLoader);
}
