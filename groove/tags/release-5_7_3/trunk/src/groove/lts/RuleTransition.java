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

import groove.control.instance.Step;
import groove.grammar.host.HostGraphMorphism;
import groove.grammar.host.HostNode;
import groove.transform.Proof;
import groove.transform.RuleApplication;
import groove.transform.RuleEvent;

import java.util.List;

/**
 *
 * @author Arend Rensink
 * @version $Revision$
 */
public interface RuleTransition extends RuleTransitionStub, GraphTransition {
    /* Overrides the method to specialise the result type. */
    @Override
    GraphState source();

    /* Overrides the method to specialise the result type. */
    @Override
    GraphState target();

    /* Overrides the method to specialise the result type. */
    @Override
    RuleEvent getEvent();

    /** Overrides the method to specialise the result type. */
    @Override
    RuleTransitionLabel label();

    /** Callback method to construct a rule application from this
     * graph transition.
     */
    public RuleApplication createRuleApplication();

    /** Returns the control step associated with this transition. */
    Step getStep();

    @Override
    public MatchResult getKey();

    /**
     * Returns the list of concrete arguments of this transition.
     */
    public List<HostNode> getArguments();

    /**
     * Returns the nodes added by this transition, in coanchor order.
     */
    public HostNode[] getAddedNodes();

    /**
     * Returns the proof of the matching of the LHS into the source graph.
     */
    public Proof getProof();

    /**
     * Returns the (partial) morphism from the source to the target graph.
     */
    @Override
    public HostGraphMorphism getMorphism();

    /**
     * Indicates if the transition involves a non-trivial symmetry. This is the
     * case if and only if there is a non-trivial isomorphism from the directly
     * derived target of the event applied to the source, to the actual (stored)
     * target.
     * @return <code>true</code> if the transition involves a non-trivial
     *         symmetry
     * @see #getMorphism()
     */
    @Override
    public boolean isSymmetry();

    /**
     * Converts this transition to a more memory-efficient representation, from
     * which the original transition can be retrieved by
     * {@link GraphTransitionStub#toTransition(GraphState)}.
     */
    @Override
    public RuleTransitionStub toStub();
}