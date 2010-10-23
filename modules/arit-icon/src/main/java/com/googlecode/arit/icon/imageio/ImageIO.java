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
package com.googlecode.arit.icon.imageio;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Iterator;

import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.spi.ImageOutputStreamSpi;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

/**
 * Replacement for {@link javax.imageio.ImageIO} with better support for Web application
 * deployments. The standard ImageIO class that is part of the JRE was developed with applets in
 * mind and causes issues when used in J2EE applications. One problem is that by default it uses a
 * disk based cache, although in most cases this is not necessary (at least when working with small
 * images). This implies that in an environment where Java 2 security is enabled, the application
 * must be granted permissions to write to the temporary directory used by ImageIO (which is
 * generally not the same as the temporary directory assigned to a Web application via the
 * <code>javax.servlet.context.tempdir</code> attribute of the servlet context).
 * <p>
 * The configuration of the standard ImageIO class can be changed to disable disk based caching or
 * to use a different temporary directory, but the scope of this configuration is the thread group.
 * This is appropriate for applets, but not for J2EE applications, because changing this
 * configuration may impact other applications running in the same container.
 * <p>
 * This class provides methods that disable disk based caching without the need to change the thread
 * group wide configuration.
 * 
 * @author Andreas Veithen
 */
public class ImageIO {
    private static final IIORegistry registry = IIORegistry.getDefaultInstance();
    
    public static BufferedImage read(InputStream in) throws IOException {
        return javax.imageio.ImageIO.read(createImageInputStream(in));
    }

    private static ImageInputStream createImageInputStream(InputStream in) throws IOException {
        for (Iterator<ImageInputStreamSpi> it = registry.getServiceProviders(ImageInputStreamSpi.class, true); it.hasNext(); ) {
            ImageInputStreamSpi spi = it.next();
            if (spi.getInputClass().isInstance(in)) {
                return spi.createInputStreamInstance(in, false, null);
            }
        }
        return null;
    }

    public static BufferedImage read(URL url) throws IOException {
        InputStream in = url.openStream();
        try {
            return read(in);
        } finally {
            in.close();
        }
    }

    public static boolean write(RenderedImage im, String format, OutputStream out) throws IOException {
        ImageOutputStream stream = createImageOutputStream(out);
        try {
            return javax.imageio.ImageIO.write(im, format, stream);
        } finally {
            stream.close();
        }
    }

    private static ImageOutputStream createImageOutputStream(OutputStream out) throws IOException {
        for (Iterator<ImageOutputStreamSpi> it = registry.getServiceProviders(ImageOutputStreamSpi.class, true); it.hasNext(); ) {
            ImageOutputStreamSpi spi = it.next();
            if (spi.getOutputClass().isInstance(out)) {
                return spi.createOutputStreamInstance(out, false, null);
            }
        }
        return null;
    }
}
