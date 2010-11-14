/*
 * Copyright 2010 Andreas Veithen
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
package com.googlecode.arit.systest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.commons.io.IOUtils;

public class Application {
    private final URL url;
    private final File tmpDir;
    private File explodedWAR;
    
    Application(URL url, File tmpDir) {
        this.url = url;
        this.tmpDir = tmpDir;
    }
    
    public File getExplodedWAR() {
        if (explodedWAR == null) {
            File explodedDir = new File(tmpDir, "exploded");
            explodedDir.mkdir();
            String file = url.getFile();
            explodedWAR = new File(explodedDir, file.substring(file.lastIndexOf('/')));
            try {
                InputStream in = url.openStream();
                try {
                    JarInputStream jar = new JarInputStream(in);
                    JarEntry jarEntry;
                    while ((jarEntry = jar.getNextJarEntry()) != null) {
                        File dest = new File(explodedWAR, jarEntry.getName());
                        if (jarEntry.isDirectory()) {
                            dest.mkdir();
                        } else {
                            dest.getParentFile().mkdirs();
                            OutputStream out = new FileOutputStream(dest);
                            try {
                                IOUtils.copy(jar, out);
                            } finally {
                                out.close();
                            }
                        }
                    }
                } finally {
                    in.close();
                }
            } catch (IOException ex) {
                throw new SystestException("Failed to explode WAR", ex);
            }
        }
        return explodedWAR;
    }
}
