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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.googlecode.arit.icon.imageio.ImageIO;

public abstract class GeneratedIconProvider implements IconProvider {
    public final ImageFormat getIconFormat() {
        return ImageFormat.PNG;
    }

    public final InputStream getIconContent() throws IOException {
        BufferedImage outImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics g = outImage.getGraphics();
        draw((Graphics2D)g);
        g.dispose();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(outImage, "PNG", baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }

    protected abstract void draw(Graphics2D g);
}
