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

import groove.grammar.host.HostEdge;
import groove.grammar.host.HostNode;
import groove.graph.AEdge;
import groove.graph.plain.PlainLabel;
import groove.util.Fixable;

/**
 *  Common implementation of pattern edges of a pattern graph.
 *
 * @author Eduardo Zambon
 */
public abstract class AbstractPatternEdge<N extends AbstractPatternNode> extends
    AEdge<N,PlainLabel> implements Fixable {
    /**
     * Constructs a new pattern edge, with the given number, source and target.
     * Pattern edges have a number for identification purposes,
     * but the number is not meant to distinguish edges.
     */
    public AbstractPatternEdge(int nr, N source, PlainLabel label, N target) {
        super(source, label, target, nr);
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return source().toString() + "--" + getIdStr() + "-->" + target().toString();
    }

    @Override
    public boolean isSimple() {
        return true;
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

    /** Returns the Id of this edge as a string. */
    public String getIdStr() {
        return getToStringPrefix() + getNumber();
    }

    /** Returns the image of the given node in the morphism. */
    public HostNode getImage(HostNode node) {
        return getMorphism().getImage(node);
    }

    /** Returns the image of the given edge in the morphism. */
    public HostEdge getImage(HostEdge edge) {
        return getMorphism().getImage(edge);
    }

    /**
     * Returns the pre-image of the given node in the morphism. The returned
     * result is a single element instead of a set because the morphism is
     * injective. May return null if the node has no pre-image.
     */
    public HostNode getPreImage(HostNode node) {
        return getMorphism().getPreImage(node);
    }

    /**
     * Returns the pre-image of the given edge in the morphism. The returned
     * result is a single element instead of a set because the morphism is
     * injective. May return null if the edge has no pre-image.
     */
    public HostEdge getPreImage(HostEdge edge) {
        return getMorphism().getPreImage(edge);
    }

    /** Returns true if the given node is in the domain of the morphism. */
    public boolean isDom(HostNode node) {
        return getMorphism().isDom(node);
    }

    /** Returns true if the given edge is in the domain of the morphism. */
    public boolean isDom(HostEdge edge) {
        return getMorphism().isDom(edge);
    }

    /** Returns true if the given node is in the co-domain of the morphism. */
    public boolean isCod(HostNode node) {
        return getMorphism().isCod(node);
    }

    /** Returns true if the given edge is in the co-domain of the morphism. */
    public boolean isCod(HostEdge edge) {
        return getMorphism().isCod(edge);
    }

}
