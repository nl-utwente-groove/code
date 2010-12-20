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
package groove.abstraction.lts;

import groove.abstraction.Shape;
import groove.control.CtrlTransition;
import groove.lts.DerivationLabel;
import groove.lts.GraphNextState;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.GraphTransitionStub;
import groove.trans.HostGraphMorphism;
import groove.trans.HostNode;
import groove.trans.RuleEvent;
import groove.trans.RuleMatch;

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

    /** Default constructor, delegates to super class. */
    public ShapeNextState(Shape shape, ShapeState source, RuleEvent event) {
        super(shape,
            source.getCtrlState().getTransition(event.getRule()).target());
        this.transition = new ShapeTransition(source, event, this);
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        boolean result;
        if (this == o) {
            result = true;
        } else if (!(o instanceof ShapeNextState)) {
            result = false;
        } else {
            ShapeNextState other = (ShapeNextState) o;
            result = getGraph().equals(other.getGraph());
            result = result && super.equals(other);
            // The targets of this.transition and other.transition cannot
            // be compared with equals, as they are both null
            result =
                result
                    && this.transition.source().equals(
                        other.transition.source())
                    && this.transition.label().equals(other.transition.label());

        }
        // Check for consistency between equals and hashCode.
        assert (!result || this.hashCode() == o.hashCode());
        return result;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + this.transition.hashCode();
        return result;
    }

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
    public boolean isNodeType() {
        return false;
    }

    @Override
    public boolean isFlag() {
        return false;
    }

    @Override
    public boolean isBinary() {
        return true;
    }

    @Override
    public HostNode[] getAddedNodes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public RuleEvent getEvent() {
        return this.transition.getEvent();
    }

    @Override
    public RuleMatch getMatch() {
        return this.transition.getMatch();
    }

    @Override
    public HostGraphMorphism getMorphism() {
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
}
