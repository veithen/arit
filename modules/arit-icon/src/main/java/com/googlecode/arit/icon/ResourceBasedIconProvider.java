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
package com.googlecode.arit.icon;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public abstract class ResourceBasedIconProvider implements IconProvider {
    private final ImageFormat iconFormat;
    private final URL iconResource;
    
    public ResourceBasedIconProvider(ImageFormat iconFormat, URL iconResource) {
        this.iconFormat = iconFormat;
        this.iconResource = iconResource;
    }

    public final ImageFormat getIconFormat() {
        return iconFormat;
    }

    public final URL getIconResource() {
        return iconResource;
    }

    public final InputStream getIconContent() throws IOException {
        return iconResource.openStream();
    }
}
