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
package com.googlecode.arit.maven;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import org.codehaus.plexus.component.annotations.Component;

import com.googlecode.arit.ModuleIdentityProviderPlugin;

@Component(role=ModuleIdentityProviderPlugin.class, hint="maven")
public class MavenModuleIdentityProviderPlugin implements ModuleIdentityProviderPlugin {
    public boolean isAvailable() {
        return true;
    }
    
    private File getSingleSubdirectory(File dir) {
        File[] subDirs = dir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isDirectory() && pathname.getName().charAt(0) != '.';
            }
        });
        return subDirs != null && subDirs.length == 1 ? subDirs[0] : null;
    }
    
    public String getModuleIdentity(URL url, ClassLoader classLoader) {
        if (url == null) {
            return null;
        }
        File deploymentDir;
        try {
            deploymentDir = new File(url.toURI());
        } catch (URISyntaxException ex) {
            return null;
        } catch (IllegalArgumentException ex) {
            // We get here if the URL is not a file: URL
            return null;
        }
        File groupDirectory = getSingleSubdirectory(new File(deploymentDir, "META-INF/maven"));
        if (groupDirectory == null) {
            return null;
        }
        File artifactDirectory = getSingleSubdirectory(groupDirectory);
        if (artifactDirectory == null) {
            return null;
        }
        File pomPropertiesFile = new File(artifactDirectory, "pom.properties");
        if (!pomPropertiesFile.exists()) {
            return null;
        }
        Properties pomProperties = new Properties();
        try {
            InputStream in = new FileInputStream(pomPropertiesFile);
            try {
                pomProperties.load(in);
            } finally {
                in.close();
            }
        } catch (IOException ex) {
            return null;
        }
        return pomProperties.getProperty("groupId") + ":" + pomProperties.getProperty("artifactId") + ":" + pomProperties.getProperty("version");
    }
}
