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
package com.googlecode.arit.servlet;

import java.io.IOException;
import java.io.InputStream;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.util.IOUtil;

import com.googlecode.arit.IconProvider;

@Component(role=IconVariant.class, hint="default")
public class DefaultIconVariant implements IconVariant {
    public ImageData createIconImage(IconProvider iconProvider) {
        try {
            InputStream in = iconProvider.getIconResource().openStream();
            try {
                return new ImageData(iconProvider.getIconFormat(), IOUtil.toByteArray(in));
            } finally {
                in.close();
            }
        } catch (IOException ex) {
            throw new IconException(ex);
        }
    }
}
