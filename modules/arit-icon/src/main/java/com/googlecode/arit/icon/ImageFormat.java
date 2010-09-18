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
package com.googlecode.arit.icon;

public enum ImageFormat {
    GIF("image/gif", "gif"),
    PNG("image/png", "png");
    
    private final String contentType;
    private final String suffix;

    private ImageFormat(String contentType, String suffix) {
        this.contentType = contentType;
        this.suffix = suffix;
    }

    public String getContentType() {
        return contentType;
    }

    public String getSuffix() {
        return suffix;
    }
}
