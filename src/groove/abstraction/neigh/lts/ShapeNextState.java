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
 * $Id$
 */
package groove.abstraction.neigh.lts;

import groove.abstraction.neigh.shape.Shape;
import groove.control.CtrlStep;
import groove.grammar.Recipe;
import groove.grammar.Rule;
import groove.grammar.host.HostGraphMorphism;
import groove.grammar.host.HostNode;
import groove.grammar.model.FormatException;
import groove.graph.EdgeRole;
import groove.lts.GraphNextState;
import groove.lts.GraphState;
import groove.lts.GraphTransitionKey;
import groove.lts.MatchResult;
import groove.lts.RuleTransition;
import groove.lts.RuleTransitionLabel;
import groove.lts.RuleTransitionStub;
import groove.transform.Proof;
import groove.transform.RuleApplication;
import groove.transform.RuleEvent;

import java.util.Collections;

/**
 * Combines a {@link ShapeState} and a {@link ShapeTransition}.
 * 
 * @author Eduardo Zambon
 */
public final class ShapeNextState extends ShapeState implements GraphNextState, RuleTransitionStub {

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    /** First transition that lead to this state. */
    private final ShapeTransition transition;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Default constructor, delegates to super class. 
     * @param number the number of the state to be created; non-negative
     */
    public ShapeNextState(int number, Shape shape, ShapeState source, MatchResult match) {
        super(source.getCacheReference(), shape, match.getStep().target(), number);
        this.transition = new ShapeTransition(source, match, this);
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public ShapeState source() {
        return this.transition.source();
    }

    @Override
    public ShapeState target() {
        return this;
    }

    @Override
    public boolean isLoop() {
        return source() == target();
    }

    @Override
    public RuleTransitionLabel label() {
        return this.transition.label();
    }

    @Override
    public String text(boolean anchored) {
        return label().text(anchored);
    }

    @Override
    public EdgeRole getRole() {
        if (getEvent().getRule().isModifying() || getStep().isModifying()) {
            return EdgeRole.BINARY;
        } else {
            return EdgeRole.FLAG;
        }
    }

    @Override
    public RuleEvent getEvent() {
        return this.transition.getEvent();
    }

    @Override
    public Rule getAction() {
        return getEvent().getRule();
    }

    @Override
    public RuleTransition getInitial() {
        return this;
    }

    @Override
    public Iterable<RuleTransition> getSteps() {
        return Collections.<RuleTransition>singletonList(this);
    }

    @Override
    public String getOutputString() throws FormatException {
        return this.transition.getOutputString();
    }

    @Override
    public Proof getProof() {
        return this.transition.getProof();
    }

    @Override
    public GraphTransitionKey getKey(GraphState source) {
        return this.transition.getKey();
    }

    @Override
    public ShapeState getTarget(GraphState source) {
        return this;
    }

    @Override
    public ShapeTransition toTransition(GraphState source) {
        return this.transition;
    }

    @Override
    public CtrlStep getStep() {
        return this.transition.getStep();
    }

    @Override
    public MatchResult getKey() {
        return new MatchResult(this);
    }

    @Override
    public boolean isPartial() {
        return getRecipe() != null;
    }

    @Override
    public Recipe getRecipe() {
        return getStep().getRecipe();
    }

    // ------------------------------------------------------------------------
    // Unimplemented methods
    // ------------------------------------------------------------------------

    @Override
    public HostNode[] getAddedNodes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public HostGraphMorphism getMorphism() {
        throw new UnsupportedOperationException();
    }

    @Override
    public RuleApplication createRuleApplication() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSymmetry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public RuleTransitionStub toStub() {
        throw new UnsupportedOperationException();
    }

    @Override
    public HostNode[] getAddedNodes(GraphState source) {
        throw new UnsupportedOperationException();
    }

}
