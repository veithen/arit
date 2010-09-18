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

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.codehaus.plexus.component.annotations.Component;

import com.googlecode.arit.IconProvider;
import com.googlecode.arit.ImageFormat;

@Component(role=IconVariant.class, hint="grayed")
public class Grayed implements IconVariant {
    public ImageData createIconImage(IconProvider iconProvider) {
        try {
            BufferedImage image = ImageIO.read(iconProvider.getIconResource());
            ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
            op.filter(image, image);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", baos);
            return new ImageData(ImageFormat.PNG, baos.toByteArray());
        } catch (IOException ex) {
            throw new IconException(ex);
        }
    }
}
