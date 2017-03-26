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
package groove.gui.look;

import java.util.HashMap;
import java.util.Map;

import org.jgraph.graph.GraphConstants;

/**
 * Edge end decorations.
 * @author Arend Rensink
 * @version $Revision $
 */
public enum EdgeEnd {
    /** Filled arrow decoration. */
    ARROW(GraphConstants.ARROW_CLASSIC),
    /** No end decoration. */
    NONE(GraphConstants.ARROW_NONE),
    /** Open (inheritance-style) arrow decoration. */
    SUBTYPE(GraphConstants.ARROW_TECHNICAL, GraphConstants.DEFAULTDECORATIONSIZE + 5, false),
    /** Composite type edge arrow decoration. */
    COMPOSITE(GraphConstants.ARROW_DIAMOND, 15),
    /** Quantifier nesting arrow. */
    NESTING(GraphConstants.ARROW_SIMPLE, GraphConstants.DEFAULTDECORATIONSIZE - 5),
    /** Simple arrow decoration. */
    SIMPLE(GraphConstants.ARROW_SIMPLE),
    /** Unfilled arrow decoration. */
    UNFILLED(GraphConstants.ARROW_CLASSIC, false);

    /** Creates an instance with a given edge code. */
    private EdgeEnd(int code) {
        this(code, GraphConstants.DEFAULTDECORATIONSIZE, true);
    }

    /** Creates an instance with a given edge code and size. */
    private EdgeEnd(int code, int size) {
        this(code, size, true);
    }

    /** Creates an instance with a given edge code and filling. */
    private EdgeEnd(int code, boolean filled) {
        this(code, GraphConstants.DEFAULTDECORATIONSIZE, filled);
    }

    /** Creates an instance with a given edge code, size, and filling. */
    private EdgeEnd(int code, int size, boolean filled) {
        this.code = code;
        this.size = size;
        this.filled = filled;
    }

    /** Returns the JGraph integer code for this edge end decoration. */
    public int getCode() {
        return this.code;
    }

    /** Returns the JGraph decoration size for this edge end decoration. */
    public int getSize() {
        return this.size;
    }

    /** Indicates if the end should be filled. */
    public boolean isFilled() {
        return this.filled;
    }

    private final int code;
    private final int size;
    private final boolean filled;

    /** Returns the unique edge end decoration for a given numerical code. */
    public static EdgeEnd getEdgeEnd(int code) {
        EdgeEnd result = codeMap.get(code);
        if (result == null) {
            throw new IllegalArgumentException(String.format("Unknown edge style code %s", code));
        }
        return result;
    }

    private static Map<Integer,EdgeEnd> codeMap = new HashMap<>();

    static {
        for (EdgeEnd style : EdgeEnd.values()) {
            codeMap.put(style.getCode(), style);
        }
    }
}
