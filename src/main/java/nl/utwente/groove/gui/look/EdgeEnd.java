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
package nl.utwente.groove.gui.look;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Edge end decorations.
 * How each decoration is drawn is up to the rendering library;
 * this enum only fixes the intended shape, size and filling.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public enum EdgeEnd {
    /** Filled arrow decoration. */
    ARROW,
    /** No end decoration. */
    NONE,
    /** Open (inheritance-style) arrow decoration. */
    SUBTYPE(EdgeEnd.DEFAULT_SIZE + 5, false),
    /** Composite type edge arrow decoration. */
    COMPOSITE(15),
    /** Quantifier nesting arrow. */
    NESTING(EdgeEnd.DEFAULT_SIZE - 5),
    /** Simple arrow decoration. */
    SIMPLE,
    /** Unfilled arrow decoration. */
    UNFILLED(false);

    /** Creates an instance with the default size, filled. */
    private EdgeEnd() {
        this(DEFAULT_SIZE, true);
    }

    /** Creates an instance with a given size, filled. */
    private EdgeEnd(int size) {
        this(size, true);
    }

    /** Creates an instance with the default size and given filling. */
    private EdgeEnd(boolean filled) {
        this(DEFAULT_SIZE, filled);
    }

    /** Creates an instance with a given size and filling. */
    private EdgeEnd(int size, boolean filled) {
        this.size = size;
        this.filled = filled;
    }

    /** Returns the decoration size, in pixels. */
    public int getSize() {
        return this.size;
    }

    /** Indicates if the end should be filled. */
    public boolean isFilled() {
        return this.filled;
    }

    private final int size;
    private final boolean filled;

    /** Default decoration size, in pixels. */
    private static final int DEFAULT_SIZE = 10;
}
