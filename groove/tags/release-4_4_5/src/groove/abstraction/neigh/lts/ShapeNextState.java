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
import groove.control.CtrlTransition;
import groove.graph.EdgeRole;
import groove.lts.DerivationLabel;
import groove.lts.GraphNextState;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.GraphTransitionStub;
import groove.trans.HostGraphMorphism;
import groove.trans.HostNode;
import groove.trans.Proof;
import groove.trans.RuleApplication;
import groove.trans.RuleEvent;
import groove.view.FormatException;

/**
 * Combines a {@link ShapeState} and a {@link ShapeTransition}.
 * 
 * @author Eduardo Zambon
 */
public final class ShapeNextState extends ShapeState implements GraphNextState,
        GraphTransitionStub {

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    private final ShapeTransition transition;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Default constructor, delegates to super class. 
     * @param number the number of the state to be created; non-negative
     */
    public ShapeNextState(int number, Shape shape, ShapeState source,
            RuleEvent event) {
        super(shape,
            source.getCtrlState().getTransition(event.getRule()).target(),
            number);
        this.transition = new ShapeTransition(source, event, this);
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public GraphState source() {
        return this.transition.source();
    }

    @Override
    public GraphState target() {
        return this;
    }

    @Override
    public DerivationLabel label() {
        return this.transition.label();
    }

    @Override
    public EdgeRole getRole() {
        if (getEvent().getRule().isModifying()
            || getCtrlTransition().isModifying()) {
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
    public String getOutputString() throws FormatException {
        return this.transition.getOutputString();
    }

    @Override
    public Proof getMatch() {
        return this.transition.getMatch();
    }

    @Override
    public RuleEvent getEvent(GraphState source) {
        return this.transition.getEvent();
    }

    @Override
    public GraphState getTarget(GraphState source) {
        return this;
    }

    @Override
    public GraphTransition toTransition(GraphState source) {
        return this.transition;
    }

    @Override
    public CtrlTransition getCtrlTransition() {
        return this.transition.getCtrlTransition();
    }

    /** Sets the index of the transition in the state transition set. */
    public void setIndex(int index) {
        this.transition.setIndex(index);
    }

    /** Gets the index of the transition in the state transition set. */
    public int getIndex() {
        return this.transition.getIndex();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ShapeNextState
            && this.getIndex() == ((ShapeNextState) obj).getIndex();
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
    public GraphTransitionStub toStub() {
        throw new UnsupportedOperationException();
    }

    @Override
    public HostNode[] getAddedNodes(GraphState source) {
        throw new UnsupportedOperationException();
    }

}