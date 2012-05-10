/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.abstraction.pattern.shape;

import groove.graph.Element;
import groove.graph.Node;
import groove.graph.TypeEdge;
import groove.util.Fixable;
import groove.view.FormatException;

/**
 * Pattern node of a pattern type graph.
 * 
 * @author Eduardo Zambon
 */
public final class TypeNode implements Node, TypeElement, Fixable {

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    /** The number of this node. */
    private final int nr;
    /** The simple graph pattern associated with this node. */
    private final SimpleGraph pattern;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** 
     * Constructs a new type node, with the given number.
     */
    public TypeNode(int nr, SimpleGraph pattern) {
        this.nr = nr;
        this.pattern = pattern;
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public int compareTo(Element obj) {
        if (obj instanceof TypeNode) {
            int otherNr = ((TypeNode) obj).nr;
            return (this.nr < otherNr ? -1 : (this.nr == otherNr ? 0 : 1));
        } else {
            assert obj instanceof TypeEdge;
            // Nodes come before edges with the node as source.
            int result = compareTo(((TypeEdge) obj).source());
            if (result == 0) {
                result = -1;
            }
            return result;
        }
    }

    @Override
    public int getNumber() {
        return this.nr;
    }

    @Override
    public void setFixed() throws FormatException {
        getPattern().setFixed();
    }

    @Override
    public boolean isFixed() {
        return getPattern().isFixed();
    }

    @Override
    public void testFixed(boolean fixed) {
        if (isFixed() != fixed) {
            throw new IllegalStateException();
        }
    }

    @Override
    public String toString() {
        return "p" + this.nr;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Return the simple graph pattern associated with this node. */
    public SimpleGraph getPattern() {
        return this.pattern;
    }

}
