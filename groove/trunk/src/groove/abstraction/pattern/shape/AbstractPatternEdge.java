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

import groove.graph.AbstractEdge;
import groove.graph.DefaultLabel;
import groove.graph.EdgeRole;
import groove.trans.HostNode;
import groove.util.Fixable;

/**
 *  Common implementation of pattern edges of a pattern graph.
 * 
 * @author Eduardo Zambon
 */
public abstract class AbstractPatternEdge<N extends AbstractPatternNode>
        extends AbstractEdge<N,DefaultLabel> implements Fixable {

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    /** The number of this edge. */
    private final int nr;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** 
     * Constructs a new pattern edge, with the given number, source and target.
     */
    public AbstractPatternEdge(int nr, N source, DefaultLabel label, N target) {
        super(source, label, target);
        this.nr = nr;
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public EdgeRole getRole() {
        return EdgeRole.BINARY;
    }

    @Override
    public String toString() {
        return source().toString() + "--" + getIdStr() + "-->"
            + target().toString();
    }

    @Override
    abstract public boolean setFixed();

    @Override
    abstract public boolean isFixed();

    @Override
    public void testFixed(boolean fixed) {
        if (isFixed() != fixed) {
            throw new IllegalStateException();
        }
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Returns the prefix for the {@link #toString()} methods. */
    abstract protected String getToStringPrefix();

    /** Returns a string that can be used when displaying a jGraph. */
    abstract public String getPrintableLabel();

    /** Return the simple graph morphism associated with this edge. */
    abstract public SimpleMorphism getMorphism();

    /** Returns the unique identifier of this edge. */
    public int getNumber() {
        return this.nr;
    }

    /** Returns the Id of this edge as a string. */
    public String getIdStr() {
        return getToStringPrefix() + this.nr;
    }

    /** Returns the non-null image of the given node in the morphism. */
    public HostNode getImage(HostNode node) {
        return getMorphism().getImage(node);
    }

    /**
     * Returns the pre-image of the given node in the morphism. The returned
     * result is a single element instead of a set because the morphism is
     * injective. May return null if the node has no pre-image. 
     */
    public HostNode getPreImage(HostNode node) {
        return getMorphism().getPreImage(node);
    }

    /** Returns true if the given node is the domain of the morphism. */
    public boolean isDom(HostNode node) {
        return getMorphism().isDom(node);
    }

    /** Returns true if the given node is the co-domain of the morphism. */
    public boolean isCod(HostNode node) {
        return getMorphism().isCod(node);
    }

}
