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

import groove.util.line.Line.ColorType;
import groove.util.line.Line.Style;

import java.awt.Color;

/**
 * Strategy for converting a {@link Line} to a {@link String}.
 * @author Rensink
 * @version $Revision $
 */
abstract public class LineFormat<R extends LineFormat.Builder<R>> {
    /** Converts a given Line to a String representation. */
    public String toString(Line line) {
        return line.toString(this).toString();
    }

    /**
     * Constructs a multiline rendering.
     * This default implementation concatenates the fragments, while
     * inserting a #getLineBreak() in between.
     */
    public R applyMulti(R[] sublines) {
        R result;
        if (sublines.length == 0) {
            result = createResult();
        } else {
            result = sublines[0];
            for (int i = 1; i < sublines.length; i++) {
                result.appendLineBreak();
                result.append(sublines[i]);
            }
        }
        return result;
    }

    /**
     * Constructs a composed rendering.
     * This default implementation just concatenates the fragments.
     */
    public R applyComposed(R[] fragments) {
        R result;
        if (fragments.length == 0) {
            result = createResult();
        } else {
            result = fragments[0];
            for (int i = 1; i < fragments.length; i++) {
                result.append(fragments[i]);
            }
        }
        return result;
    }

    /** Constructs a coloured rendering. */
    abstract public R applyColored(ColorType type, Color color, R subline);

    /** Constructs a styled rendering. */
    abstract public R applyStyled(Style style, R subline);

    /** Constructs a rendering of a horizontal rule. */
    abstract public R createHRule();

    /** Constructs a rendering of an unstructured string. */
    abstract public R applyAtomic(String text);

    /** Callback method to create a result object. */
    abstract public R createResult();

    /** Result type, to be passed around during the construction. */
    public interface Builder<R extends Builder<R>> {
        /** Indicates if the result is as yet empty. */
        boolean isEmpty();

        /** Returns a string representation of the result. */
        StringBuilder getResult();

        /**
         * Appends another result to this one.
         */
        void append(R other);

        /** Appends a line break to this result. */
        void appendLineBreak();
    }
}
