/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.util.line;

import java.util.HashMap;
import java.util.Map;

import nl.utwente.groove.util.Exceptions;

/**
 * Edge layout line styles.
 * The numeric codes are persisted in the layout information of saved graphs,
 * and (except for {@link #MANHATTAN}) coincide with the corresponding
 * {@code org.jgraph.graph.GraphConstants.STYLE_} constants by which the GUI
 * renders them; they must not be changed.
 * @author Arend Rensink
 * @version $Revision$
 */
public enum LineStyle {
    /** Orthogonal line style. */
    ORTHOGONAL(11, "Orthogonal"),
    /** Splined line style. */
    SPLINE(13, "Spline"),
    /** Bezier curved line style. */
    BEZIER(12, "Bezier"),
    /** Manhattan skyline style (only horizontal and vertical). */
    MANHATTAN(14, "Manhattan");

    private LineStyle(int code, String name) {
        this.code = code;
        this.name = name;
    }

    /** Returns a number coding for the line style. */
    public int getCode() {
        return this.code;
    }

    /** Returns the name of this line style. */
    public String getName() {
        return this.name;
    }

    /** Indicates if this is the default line style. */
    public boolean isDefault() {
        return this == DEFAULT_VALUE;
    }

    private final int code;
    private final String name;

    /** Indicates if a given code stands for a valid line style. */
    public static boolean isStyle(int code) {
        return codeMap.containsKey(code);
    }

    /** Returns the unique line style for a given numerical code. */
    public static LineStyle getStyle(int code) {
        LineStyle result = codeMap.get(code);
        if (result == null) {
            throw Exceptions.illegalArg("Unknown line style code %s", code);
        }
        return result;
    }

    /** The default line style. */
    static public final LineStyle DEFAULT_VALUE = ORTHOGONAL;

    private static final Map<Integer,LineStyle> codeMap = new HashMap<>();

    static {
        for (LineStyle style : LineStyle.values()) {
            codeMap.put(style.getCode(), style);
        }
    }
}
