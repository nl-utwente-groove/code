// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id$
 */
package groove.util;

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * Helper class for accessing colors by name.
 * @author Arend Rensink
 * @version $Revision$
 */
public class Colors {
    /**
     * Key suffix for colors one step brighter than the original.
     * @see Color#brighter()
     */
    public static final String BRIGHTER = ".brighter";
    /**
     * Key suffix for colors one step darker than the original.
     * @see Color#darker()
     */
    public static final String DARKER = ".darker";

    /**
     * Returns a map from color names to colors. The keys include all field
     * names from <tt>java.awt.Color</tt> as strings, plus keys
     * <tt>key.darker</tt> and <tt>key.brighter</tt> for each of
     * <tt>key</tt>.
     * @return An (unmodifiable) map from <tt>String</tt> values to
     *         <tt>Color</tt> instances.
     */
    public static Map<String,Color> getColorMap() {
        return Collections.unmodifiableMap(colorMap);
    }

    /**
     * Searches for a color with a given name or key, given as a string. The
     * color is searched using the following criteria:
     * <ul>
     * <li> If the name is one of the keys in <tt>getColorMap()</tt> then the
     * corresponding key is returned;
     * <li> Otherwise, if the name is a system property (recognised by
     * <tt>Color.getColor(String)</tt>) then the corresponding color is
     * returned;
     * <li> Otherwise, if the name is a color key recognised by
     * <tt>Color.decode(String)</tt>) then the corresponding color is
     * returned;
     * <li> Otherwise, if the name is a space-separated sequence of three or
     * four byte values standing for the red, green and blue components, and
     * optionally an alpha value, then the corresponding color is returned using
     * the appropriate <tt>Color</tt> method;
     * <li> Otherwise, the color cannot be found and <tt>null</tt> is
     * returned.
     * </ul>
     * @param name the name or key under which the color is sought
     * @return the color found for <tt>name</tt>, or <tt>null</tt> if no
     *         such color is found.
     * @see #getColorMap()
     * @see Color#getColor(String)
     */
    public static Color findColor(String name) {
        if (colorMap.containsKey(name)) {
            return colorMap.get(name);
        }
        Color result = Color.getColor(name);
        if (result != null) {
            return result;
        }
        try {
            return Color.decode(name);
        } catch (NumberFormatException exc) {
            // proceed
        }
        // try decompose the color as a sequence of red green blue [alpha]
        int[] val = Groove.toIntArray(name);
        if (val == null) {
            val = Groove.toIntArray(name, ",");
        }
        if (val != null) {
            if (val.length == 3) {
                return new Color(norm(val[0]), norm(val[1]), norm(val[2]));
            } else if (val.length == 4) {
                return new Color(norm(val[0]), norm(val[1]), norm(val[2]),
                    norm(val[3]));
            }
        }
        return null;
    }

    /**
     * Prints out the (key, color)-pairs in the map returned by
     * <tt>getColorMap()</tt>, in the form <tt>key = hexString</tt> (one
     * per line).
     * @see #getColorMap()
     */
    public static void main(String[] args) {
        for (Map.Entry<String,Color> colorEntry : colorMap.entrySet()) {
            String key = colorEntry.getKey();
            Color color = colorEntry.getValue();
            String colorString =
                "" + color.getRed() + " " + color.getGreen() + " "
                    + color.getBlue() + " " + color.getAlpha();
            System.out.println(key + " = " + colorString);
            assert color.equals(findColor(colorString));
        }
    }

    /**
     * A map from string keys to colors.
     */
    private static final Map<String,Color> colorMap =
        new TreeMap<>();

    /** Returns a normalised value, within the range 0..{@link #MAX}. */
    private static int norm(int val) {
        if (val < 0) {
            return 0;
        }
        if (val > MAX) {
            return MAX;
        }
        return val;
    }

    private static final int MAX = 255;
    static {
        Class<Color> colorClass = Color.class;
        Field[] colorFields = colorClass.getFields();
        for (Field field : colorFields) {
            if (Modifier.isStatic(field.getModifiers())
                && field.getType() == colorClass) {
                try {
                    String key = field.getName();
                    Color color = (Color) field.get(null);
                    colorMap.put(key, color);
                    colorMap.put(key + DARKER, color.darker());
                    colorMap.put(key + BRIGHTER, color.brighter());
                } catch (IllegalArgumentException e) {
                    // proceed
                } catch (IllegalAccessException e) {
                    // proceed
                }
            }
        }
    }
}
