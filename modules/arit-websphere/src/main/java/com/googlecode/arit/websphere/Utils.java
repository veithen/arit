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
    
    public static String getWebAppRoot(CompoundClassLoaderRBean ccl) {
        for (SinglePathClassProviderRBean provider : ccl.getProviders()) {
            String path = provider.getPath().replace('\\', '/');
            if (path.endsWith("/WEB-INF/classes")) {
                return path.substring(0, path.length()-16);
            }
        }
        return null;
    }
    
    public static URL dirToURL(String dir) {
        if (dir != null) {
            try {
                return new File(dir).toURL();
            } catch (MalformedURLException ex) {
                return null;
            }
        } else {
            return null;
        }
    }
}
