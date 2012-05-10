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

import groove.graph.Edge;
import groove.graph.EdgeRole;
import groove.graph.Element;
import groove.graph.Label;
import groove.util.Fixable;
import groove.view.FormatException;

/**
 * Pattern edge of a pattern type graph.
 * 
 * @author Eduardo Zambon
 */
public final class TypeEdge implements Edge, TypeElement, Fixable {

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    /** The number of this edge. */
    private final int nr;
    /** Source type node of this edge. */
    private final TypeNode source;
    /** Target type node of this edge. */
    private final TypeNode target;
    /** The simple graph morphism between patterns of source and target nodes. */
    private final SimpleMorphism morph;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** 
     * Constructs a new type edge, with the given number, source and target.
     */
    public TypeEdge(int nr, TypeNode source, TypeNode target,
            SimpleMorphism morph) {
        assert morph.getSource().equals(source)
            && morph.getTarget().equals(target);
        this.nr = nr;
        this.source = source;
        this.target = target;
        this.morph = morph;
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public int compareTo(Element obj) {
        if (obj instanceof TypeNode) {
            // for nodes, we just need to look at the source of this edge
            int result = source().compareTo(obj);
            // if the source equals the node, edges come later
            if (result == 0) {
                result++;
            }
            return result;
        } else {
            assert obj instanceof TypeEdge;
            int otherNr = ((TypeEdge) obj).nr;
            return (this.nr < otherNr ? -1 : (this.nr == otherNr ? 0 : 1));
        }
    }

    @Override
    public TypeNode source() {
        return this.source;
    }

    @Override
    public TypeNode target() {
        return this.target;
    }

    @Override
    public EdgeRole getRole() {
        return EdgeRole.BINARY;
    }

    @Override
    public String toString() {
        return source().toString() + "--d" + this.nr + "-->"
            + target().toString();
    }

    @Override
    public void setFixed() throws FormatException {
        getMorphism().setFixed();
    }

    @Override
    public boolean isFixed() {
        return getMorphism().isFixed();
    }

    @Override
    public void testFixed(boolean fixed) {
        if (isFixed() != fixed) {
            throw new IllegalStateException();
        }
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Returns the unique identifier of this edge. */
    public int getNumber() {
        return this.nr;
    }

    /** Return the simple graph morphism associated with this edge. */
    public SimpleMorphism getMorphism() {
        return this.morph;
    }

    // ------------------------------------------------------------------------
    // Unsupported methods
    // ------------------------------------------------------------------------

    @Override
    public Label label() {
        throw new UnsupportedOperationException();
    }

}
