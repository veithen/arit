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
package com.googlecode.arit.spring;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ColorPaletteGenerator {
    private static final float[][] sbValues = { { 0.8f, 0.8f }, { 0.8f, 0.5f }, { 0.5f, 0.8f } }; 
    
    private ColorPaletteGenerator() {}
    
    /**
     * Generate a color palette with the specified minimum size. The method
     * works with the HSB color space and will generate a sequence of colors
     * distributed uniformly over the hue component. For larger palette sizes,
     * it will also start using different saturation/brightness combinations.
     * 
     * @param minSize
     *            the minimum size of the palette
     * @return the generated color palette
     */
    public static Color[] createColorPalette(int minSize) {
        int hueSteps = 4;
        int sbCombinations = 1;
        int size;
        while (true) {
            size = hueSteps * sbCombinations;
            if (size >= minSize) {
                break;
            } else if (hueSteps > sbCombinations*5 && sbCombinations < sbValues.length) {
                sbCombinations++;
            } else {    
                hueSteps = hueSteps + 2 + hueSteps/3;
            }
        }
        
        Color[] colors = new Color[size];
        int index = 0;
        for (int i=0; i<hueSteps; i++) {
            for (int j=0; j<sbCombinations; j++) {
                colors[index++] = Color.getHSBColor((float)i/(float)hueSteps, sbValues[j][0], sbValues[j][1]);
            }
        }
        
        return colors;
    }
    
    /**
     * Assign colors to the items in the given collection. The implementation attempts to assign
     * colors such that the result is stable with respect to addition or removal of elements in the
     * collection. This means that the probability that adding or removing an element from the
     * collection would change the colors assigned to the remaining elements. Note that this only
     * works if the items in the collection implement {@link Object#hashCode()} in a meaningful way
     * and if the items in the collection have a deterministic order (e.g. if they are sorted).
     * 
     * @param <T>
     *            the type of items in the collection
     * @param collection
     *            the collection
     * @return a map that containing an entry with the assigned color for each item in the
     *         collection
     */
    public static <T> Map<T,Color> assignColors(List<T> collection) {
        Map<T,Color> result = new HashMap<T,Color>();
        Color[] palette = createColorPalette(collection.size());
        int paletteSize = palette.length;
        for (T item : collection) {
            // Note that % is not modulo, but the remainder, i.e. it can be negative
            int index = (item.hashCode() & 0x7FFFFFFF) % paletteSize;
            Color color;
            while (true) {
                color = palette[index];
                if (color != null) {
                    break;
                } else {
                    index = (index+1) % paletteSize;
                }
            }
            palette[index] = null;
            result.put(item, color);
        }
        return result;
    }
}
