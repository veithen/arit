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

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.awt.image.RenderedImage;

import org.codehaus.plexus.component.annotations.Component;

@Component(role=IconVariant.class, hint="grayed")
public class Grayed extends TransformationVariant {
    @Override
    protected RenderedImage transform(BufferedImage image) {
        // Normally it should be possible to produce a grayed image using the following code:
        //
        // ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
        // op.filter(image, image);
        //
        // However, this doesn't work on older Java versions, such as the JRE used by WAS 6.1.
        // Somehow, they don't preserve transparencey and will eventually produce an image with a
        // black background.
        //
        // The following algorithm seems to work on all JREs:
        
        GrayFilter filter = new GrayFilter();
        ImageProducer prod = new FilteredImageSource(image.getSource(), filter);
        Image grayImage = Toolkit.getDefaultToolkit().createImage(prod);

        BufferedImage rendered = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR_PRE);
        Graphics2D g = rendered.createGraphics();
        g.drawImage(grayImage, 0, 0, null);
        g.dispose();
        return rendered;
    }
}
