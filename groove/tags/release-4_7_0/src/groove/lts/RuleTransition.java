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
import groove.trans.HostGraphMorphism;
import groove.trans.HostNode;
import groove.trans.Proof;
import groove.trans.Recipe;
import groove.trans.RuleApplication;
import groove.trans.RuleEvent;
import groove.view.FormatException;

/**
 * 
 * @author Arend Rensink
 * @version $Revision$
 */
public interface RuleTransition extends RuleTransitionStub, GraphTransition {
    /** Overrides the method to specialise the result type. */
    GraphState source();

    /** Overrides the method to specialise the result type. */
    GraphState target();

    /** Returns the event associated with this rule transition. */
    RuleEvent getEvent();

    /** Overrides the method to specialise the result type. */
    RuleTransitionLabel label();

    /** Callback method to construct a rule application from this
     * graph transition.
     */
    public RuleApplication createRuleApplication();

    /** 
     * Returns a string to be sent to the standard output
     * on adding a transition with this event to a GTS. 
     * @return a standard output string, or {@code null} if
     * there is no standard output for the rule of this event.
     * @throws FormatException if the format string of the rule
     * does not correspond to the actual rule parameters
     */
    public String getOutputString() throws FormatException;

    /** Returns the (possibly {@code null} control transition associated with this transition. */
    CtrlTransition getCtrlTransition();

    @Override
    public MatchResult getKey();

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
    public boolean isSymmetry();

    /**
     * Converts this transition to a more memory-efficient representation, from
     * which the original transition can be retrieved by
     * {@link GraphTransitionStub#toTransition(GraphState)}.
     */
    public RuleTransitionStub toStub();

    /** Indicates if this rule transition is part of a recipe transition. */
    public boolean isPartial();

    /** Returns the (optional) recipe that this rule transition is part of. */
    public Recipe getRecipe();
}