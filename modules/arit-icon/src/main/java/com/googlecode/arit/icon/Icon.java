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

import java.util.HashMap;
import java.util.Map;

import com.googlecode.arit.icon.variant.IconVariant;

public class Icon {
    private final String key;
    private final IconProvider provider;
    private final Map<String,IconVariant> variants;
    private final Map<String,IconImage> images = new HashMap<String,IconImage>();

    Icon(String key, IconProvider provider, Map<String,IconVariant> variants) {
        this.key = key;
        this.provider = provider;
        this.variants = variants;
    }

    public synchronized IconImage getIconImage(String variantName) {
        IconImage image = images.get(variantName);
        if (image == null) {
            IconVariant variant = variants.get(variantName);
            if (variant == null) {
                throw new IllegalArgumentException("No such icon variant: " + variantName);
            }
            image = new IconImage(variantName, key, variant.createIconImage(provider));
            images.put(variantName, image);
        }
        return image;
    }
}
