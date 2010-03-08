package com.google.code.rex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ProviderFinder {
    private ProviderFinder() {}
    
    public static <T> List<T> find(ClassLoader classLoader, Class<T> providerType) throws ProviderFinderException {
        String resourceName = "META-INF/services/" + providerType.getName();
        Enumeration<URL> resources;
        try {
            resources = classLoader.getResources(resourceName);
        } catch (IOException ex) {
            throw new ProviderFinderException("Unable to load resource " + resourceName);
        }
        List<T> providers = new ArrayList<T>();
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        if (line.length() != 0 && line.charAt(0) != '#') {
                            Class<?> clazz;
                            try {
                                clazz = classLoader.loadClass(line);
                            } catch (ClassNotFoundException ex) {
                                throw new ProviderFinderException("Class " + line + " not found");
                            }
                            if (Provider.class.isAssignableFrom(clazz)) {
                                Provider<?> provider;
                                try {
                                    provider = (Provider<?>)clazz.newInstance();
                                } catch (Exception ex) {
                                    throw new ProviderFinderException("Unable to instantiate " + clazz, ex);
                                }
                                T impl;
                                try {
                                    impl = providerType.cast(provider.getImplementation());
                                } catch (ClassCastException ex) {
                                    throw new ProviderFinderException("Class " + clazz.getName() + " is not a provider for implementations of type " + providerType.getName());
                                }
                                if (impl != null) {
                                    providers.add(impl);
                                }
                            } else {
                                Class<? extends T> implClass;
                                try {
                                    implClass = clazz.asSubclass(providerType);
                                } catch (ClassCastException ex) {
                                    throw new ProviderFinderException("Class " + clazz.getName() + " is not of type " + providerType.getName());
                                }
                                T impl;
                                try {
                                    impl = implClass.newInstance();
                                } catch (Exception ex) {
                                    throw new ProviderFinderException("Unable to instantiate " + implClass, ex);
                                }
                                providers.add(impl);
                            }
                        }
                    }
                } finally {
                    in.close();
                }
            } catch (IOException ex) {
                throw new ProviderFinderException("Error loading " + url, ex);
            }
        }
        return providers;
    }
    
    public static <T> List<T> find(Class<T> providerType) throws ProviderFinderException {
        return find(ProviderFinder.class.getClassLoader(), providerType);
    }
}
