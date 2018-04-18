/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package groove.util.line;

import groove.gui.Options;
import groove.gui.look.Values;

import java.util.HashMap;
import java.util.Map;

import javax.swing.KeyStroke;

import org.jgraph.graph.GraphConstants;

/**
 * Edge layout line styles.
 * @author rensink
 * @version $Revision $
 */
public enum LineStyle {
    /** Orthogonal line style. */
    ORTHOGONAL(GraphConstants.STYLE_ORTHOGONAL, "Orthogonal",
            Options.ORTHOGONAL_LINE_STYLE_KEY),
    /** Splined line style. */
    SPLINE(GraphConstants.STYLE_SPLINE, "Spline", Options.SPLINE_LINE_STYLE_KEY),
    /** Bezier curved line style. */
    BEZIER(GraphConstants.STYLE_BEZIER, "Bezier", Options.BEZIER_LINE_STYLE_KEY),
    /** Manhattan skyline style (only horizontal and vertical). */
    MANHATTAN(Values.STYLE_MANHATTAN, "Manhattan",
            Options.MANHATTAN_LINE_STYLE_KEY);

    private LineStyle(int code, String name, KeyStroke stroke) {
        this.code = code;
        this.name = name;
        this.stroke = stroke;
    }

    /** Returns a number coding for the line style. */
    public int getCode() {
        return this.code;
    }

    /** Returns the name of this line style. */
    public String getName() {
        return this.name;
    }

    /** Returns the name of this line style. */
    public KeyStroke getKey() {
        return this.stroke;
    }

    /** Indicates if this is the default line style. */
    public boolean isDefault() {
        return this == DEFAULT_VALUE;
    }

    private final int code;
    private final String name;
    private final KeyStroke stroke;

    /** Indicates if a given code stands for a valid line style. */
    public static boolean isStyle(int code) {
        return codeMap.containsKey(code);
    }

    /** Returns the unique line style for a given numerical code. */
    public static LineStyle getStyle(int code) {
        LineStyle result = codeMap.get(code);
        if (result == null) {
            throw new IllegalArgumentException(String.format(
                "Unknown line style code %s", code));
        }
        return result;
    }

    /** The default line style. */
    static public final LineStyle DEFAULT_VALUE = ORTHOGONAL;

    private static final Map<Integer,LineStyle> codeMap =
        new HashMap<>();

    static {
        for (LineStyle style : LineStyle.values()) {
            codeMap.put(style.getCode(), style);
        }
    }
}
