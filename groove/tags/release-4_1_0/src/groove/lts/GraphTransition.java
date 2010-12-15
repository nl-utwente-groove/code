// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/**
 * 
 */
package groove.lts;

import groove.control.CtrlTransition;
import groove.graph.NodeEdgeMap;
import groove.trans.HostNode;
import groove.trans.RuleMatch;

/**
 * 
 * @author Arend Rensink
 * @version $Revision$
 */
public interface GraphTransition extends Transition, MatchResult {
    /** Overrides the method to specialise the result type. */
    GraphState source();

    /** Overrides the method to specialise the result type. */
    GraphState target();

    /** Returns the (possibly {@code null} control transition associated with this transition. */
    CtrlTransition getCtrlTransition();

    /**
     * Returns the nodes added by this transition, in coanchor order.
     */
    public HostNode[] getAddedNodes();

    /**
     * Returns the matching of the LHS into the source graph.
     */
    public RuleMatch getMatch();

    /**
     * Returns the (partial) morphism from the source to the target graph.
     */
    public NodeEdgeMap getMorphism();

    /**
     * Indicates if the transition involves a non-trivial symmetry. This is the
     * case if and only if there is a non-trivial isomorphism from the directly
     * derived target of the event applied to the source, to the actual (stored)
     * target.
     * @return <code>true</code> if the transition involves a non-trivial
     *         symmetry
     * @see #getMorphism()
     */
    public boolean isSymmetry();

    /**
     * Converts this transition to a more memory-efficient representation, from
     * which the original transition can be retrieved by
     * {@link GraphTransitionStub#toTransition(GraphState)}.
     */
    public GraphTransitionStub toStub();

}