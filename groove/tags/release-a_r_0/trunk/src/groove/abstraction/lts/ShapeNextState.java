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
import groove.graph.Label;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.lts.GraphNextState;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.GraphTransitionStub;
import groove.trans.RuleEvent;
import groove.trans.RuleMatch;

/**
 * Combines a {@link ShapeState} and a {@link ShapeTransition}.
 * 
 * @author Eduardo Zambon
 */
public class ShapeNextState extends ShapeState implements GraphNextState,
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
        super(shape);
        this.transition = new ShapeTransition(source, event, this);
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ShapeNextState)) {
            return false;
        }
        ShapeNextState other = (ShapeNextState) o;
        boolean result = getGraph().equals(((ShapeNextState) o).getGraph());
        result = result && super.equals(other);
        // The targets of this.transition and other.transition cannot
        // be compared with equals, as they are both null
        result =
            result
                && this.transition.source().equals(other.transition.source());
        result =
            result && this.transition.label().equals(other.transition.label());
        assert (!result || other.hashCode() == hashCode()) : "The equals method does not comply with the hash code method !!!";
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
    public Node end(int i) {
        switch (i) {
        case SOURCE_INDEX:
            return source();
        case TARGET_INDEX:
            return target();
        default:
            throw new IllegalArgumentException("Illegal end index number " + i
                + " for " + this);
        }
    }

    @Override
    public int endCount() {
        return 2;
    }

    @Override
    public int endIndex(Node node) {
        if (source().equals(node)) {
            return 0;
        }
        if (target().equals(node)) {
            return 1;
        }
        return -1;
    }

    @Override
    public Node[] ends() {
        Node[] result = new Node[2];
        result[0] = source();
        result[1] = target();
        return result;
    }

    @Override
    public boolean hasEnd(Node node) {
        return source().equals(node) || target().equals(node);
    }

    @Override
    public Label label() {
        return this.transition.label();
    }

    @Override
    public Node opposite() {
        return target();
    }

    @Override
    public Node[] getAddedNodes() {
        return this.transition.getAddedNodes();
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
    public Morphism getMorphism() {
        return this.transition.getMorphism();
    }

    @Override
    public boolean isSymmetry() {
        return this.transition.isSymmetry();
    }

    @Override
    public GraphTransitionStub toStub() {
        return this.transition.toStub();
    }

    @Override
    public Node[] getAddedNodes(GraphState source) {
        return this.transition.getAddedNodes();
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

    /** Delegates the comparison to the stored transition. */
    public boolean isEquivalent(ShapeTransition other) {
        return this.transition.isEquivalent(other);
    }

}