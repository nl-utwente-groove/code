/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: AbstractEdge.java,v 1.10 2008-01-30 09:32:57 iovka Exp $
 */
package groove.graph;

import groove.util.TreeHashSet;

/**
 * Defines a class of edges which are guaranteed to have
 * canonical representatives - meaning that the equality test can
 * be reduced to object equality.
 * Edges are also uniquely numbered, for more efficient storage
 * in a {@link TreeHashSet}.
 * @author Arend Rensink
 * @version $Revision: 2894 $
 */
public abstract class CanonicalEdge<N extends Node,L extends Label> extends
        AbstractEdge<N,L,N> {
    /**
     * Creates an edge with given end nodes and label.
     */
    protected CanonicalEdge(N source, L label, N target, int nr) {
        super(source, label, target);
        this.nr = nr;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = this == obj;
        // test that the result is the same as number equality
        // or source-label-target equality
        assert result == (obj instanceof CanonicalEdge && this.nr == ((CanonicalEdge<?,?>) obj).nr) : String.format(
            "Distinct %s and %s %s with the same number %d",
            getClass().getName(), obj.getClass().getName(), this, this.nr);
        assert result == (obj instanceof CanonicalEdge && super.equals(obj)) : String.format(
            "Distinct %s and %s %s with the same content",
            getClass().getName(), obj.getClass().getName(), this);
        return result;
    }

    /** Factory method for edges of the implementing type. */
    abstract public CanonicalEdge<N,L> newEdge(N source, L label, N target,
            int nr);

    /** 
     * Returns the number of this edge.
     * The number is guaranteed to be unique for each canonical edge representative.
     */
    public int getNumber() {
        return this.nr;
    }

    /** The (unique) number of this edge. */
    private final int nr;

    /** 
     * Interface for edge factories of {@link CanonicalEdge} specialisations.
     * In practice, canonical edge classes will be their own factories.
     */
    static public interface Factory<N extends Node,E extends Edge> {
        /** 
         * Creates a new edge of the specified type,
         * from given source, label and target.
         */
        E newEdge(N source, Label label, N target, int nr);
    }
}