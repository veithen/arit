/*
 * Copyright 2010-2011 Andreas Veithen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.arit.websphere;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public final class Utils {
    private Utils() {}
    
    public static File getWebAppRoot(CompoundClassLoaderRBean ccl) {
        for (SinglePathClassProviderRBean provider : ccl.getProviders()) {
            File path = new File(provider.getPath());
            File parent = path.getParentFile();
            if (parent != null && path.getName().equals("classes") && parent.getName().equals("WEB-INF")) {
                return parent.getParentFile();
            }
        }
        return null;
    }
    
    public static File getEARRoot(CompoundClassLoaderRBean ccl) {
        for (SinglePathClassProviderRBean provider : ccl.getProviders()) {
            File path = new File(provider.getPath());
            do {
                if (path.getName().endsWith(".ear") && new File(new File(path, "META-INF"), "application.xml").exists()) {
                    return path;
                }
                path = path.getParentFile();
            } while (path != null);
        }
        return null;
    }
    
    public static URL dirToURL(File dir) {
        if (dir != null) {
            try {
                return dir.toURL();
            } catch (MalformedURLException ex) {
                return null;
            }
        } else {
            return null;
        }
    }
}
