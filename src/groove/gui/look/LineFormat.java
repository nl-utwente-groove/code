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

import groove.gui.look.Line.Style;

import java.awt.Color;

/**
 * Strategy for converting a {@link Line} to a {@link String}.
 * @author Rensink
 * @version $Revision $
 */
abstract public class LineFormat {
    /** Converts a given Line to a String representation. */
    public String toString(Line line) {
        return line.toString(this).toString();
    }

    /**
     * Constructs a multiline rendering.
     * This default implementation concatenates the fragments, while
     * inserting a #getLineBreak() in between.
     */
    public StringBuilder applyMulti(StringBuilder[] sublines) {
        StringBuilder result;
        if (sublines.length == 0) {
            result = new StringBuilder();
        } else {
            result = sublines[0];
            for (int i = 1; i < sublines.length; i++) {
                result.append(getLineBreak());
                result.append(sublines[i]);
            }
        }
        return result;
    }

    /** String effecting a line break in a multiline rendering. */
    abstract protected String getLineBreak();

    /** 
     * Constructs a composed rendering.
     * This default implementation just concatenates the fragments.
     */
    public StringBuilder applyComposed(StringBuilder[] fragments) {
        StringBuilder result;
        if (fragments.length == 0) {
            result = new StringBuilder();
        } else {
            result = fragments[0];
            for (int i = 1; i < fragments.length; i++) {
                result.append(fragments[i]);
            }
        }
        return result;
    }

    /** Constructs a coloured rendering. */
    abstract public StringBuilder applyColored(Color color,
            StringBuilder subline);

    /** Constructs a styled rendering. */
    abstract public StringBuilder applyStyled(Style style, StringBuilder subline);

    /** Constructs a rendering of an unstructured string. */
    abstract public StringBuilder applyAtomic(String text);
}
