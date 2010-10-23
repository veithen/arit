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
package com.googlecode.arit.servlet.icon;

import java.awt.Graphics;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.RenderedImage;
import java.io.IOException;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import com.googlecode.arit.icon.imageio.ImageIO;
import com.googlecode.arit.icon.variant.IconVariant;
import com.googlecode.arit.icon.variant.TransformationVariant;

@Component(role=IconVariant.class, hint="defunct")
public class Defunct extends TransformationVariant implements Initializable {
    private BufferedImage skull;
    
    public void initialize() throws InitializationException {
        try {
            skull = ImageIO.read(Defunct.class.getResource("defunct.gif"));
        } catch (IOException ex) {
            throw new InitializationException("Failed to load defunct.gif", ex);
        }
    }

    @Override
    protected RenderedImage transform(BufferedImage image) {
        ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
        op.filter(image, image);
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage outImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = outImage.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.drawImage(skull, 0, height/2, width/2, height/2, null);
        g.dispose();
        return outImage;
    }
}
