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
package com.googlecode.arit.icon.variant;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.googlecode.arit.icon.IconProvider;
import com.googlecode.arit.icon.ImageData;
import com.googlecode.arit.icon.ImageFormat;
import com.googlecode.arit.icon.imageio.ImageIO;

public abstract class TransformationVariant implements IconVariant {
    public final ImageData createIconImage(IconProvider iconProvider) {
        try {
            InputStream in = iconProvider.getIconContent();
            try {
                BufferedImage image = ImageIO.read(in);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(transform(image), "PNG", baos);
                return new ImageData(ImageFormat.PNG, baos.toByteArray());
            } finally {
                in.close();
            }
        } catch (IOException ex) {
            throw new IconException(ex);
        }
    }

    protected abstract RenderedImage transform(BufferedImage image); 
}
