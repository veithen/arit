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

import java.awt.image.RGBImageFilter;

public class GrayFilter extends RGBImageFilter {
    public int filterRGB(int x, int y, int rgb) {
        int gray = (int)(0.30 * ((rgb >> 16) & 0xff) + 
                         0.59 * ((rgb >> 8) & 0xff) + 
                         0.11 * (rgb & 0xff));
        return (rgb & 0xff000000) | (gray << 16) | (gray << 8) | (gray << 0);
    }
}
