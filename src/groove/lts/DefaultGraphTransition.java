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
/*
 * $Id: DefaultGraphTransition.java,v 1.19 2008-03-05 16:50:10 rensink Exp $
 */
package groove.lts;

import groove.control.ControlState;
import groove.control.ControlTransition;
import groove.graph.AbstractEdge;
import groove.graph.AbstractGraphShape;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.NodeSetEdgeSetGraph;
import groove.trans.RuleApplication;
import groove.trans.RuleEvent;
import groove.trans.RuleMatch;

/**
 * Models a transition built upon a rule application
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-03-05 16:50:10 $
 */
public class DefaultGraphTransition extends
        AbstractEdge<GraphState,Label,GraphState> implements
        GraphTransitionStub, GraphTransition {
    /**
     * Constructs a GraphTransition on the basis of a given rule event, between
     * a given source and target state.
     */
    public DefaultGraphTransition(RuleEvent event, Node[] addedNodes,
            GraphState source, GraphState target, boolean symmetry) {
        super(source, event.getLabel(), target);
        this.event = event;
        this.addedNodes = addedNodes;
        this.symmetry = symmetry;
    }

    /**
     * @param source the source state
     * @param label the label of the transition
     * @param target the target state
     */
    public DefaultGraphTransition(GraphState source, Label label,
            GraphState target) {
        super(source, label, target);
        this.symmetry = false;
        this.addedNodes = null;
    }

    public RuleEvent getEvent() {
        return this.event;
    }

    public boolean isSymmetry() {
        return this.symmetry;
    }

    public Node[] getAddedNodes() {
        return this.addedNodes;
    }

    public GraphTransitionStub toStub() {
        /*if (!getEvent().getRule().isModifying()) {
            return getEvent();
        } else*/if (isSymmetry()) {
            return new SymmetryTransitionStub(getEvent(), getAddedNodes(),
                target());
        } else if (target() instanceof DefaultGraphNextState) {
            return ((DefaultGraphNextState) target()).createInTransitionStub(
                source(), getEvent(), getAddedNodes());
        } else {
            return new IdentityTransitionStub(getEvent(), getAddedNodes(),
                target());
        }
    }

    public RuleMatch getMatch() {
        return getEvent().getMatch(source().getGraph());
    }

    /**
     * This implementation throws an {@link IllegalArgumentException} if
     * <code>source</code> is not equal to the source of the transition,
     * otherwise it returns {@link #getEvent()}.
     */
    public RuleEvent getEvent(GraphState source) {
        if (source != source()) {
            throw new IllegalArgumentException("Source state incompatible");
        } else {
            return getEvent();
        }
    }

    /**
     * This implementation throws an {@link IllegalArgumentException} if
     * <code>source</code> is not equal to the source of the transition,
     * otherwise it returns {@link #getAddedNodes()}.
     */
    public Node[] getAddedNodes(GraphState source) {
        if (source != source()) {
            throw new IllegalArgumentException("Source state incompatible");
        } else {
            return getAddedNodes();
        }
    }

    /**
     * This implementation throws an {@link IllegalArgumentException} if
     * <code>source</code> is not equal to the source of the transition,
     * otherwise it returns <code>this</code>.
     */
    public GraphTransition toTransition(GraphState source) {
        if (source != source()) {
            throw new IllegalArgumentException("Source state incompatible");
        } else {
            return this;
        }
    }

    /**
     * This implementation throws an {@link IllegalArgumentException} if
     * <code>source</code> is not equal to the source of the transition,
     * otherwise it returns <code>target()</code>.
     */
    public GraphState getTarget(GraphState source) {
        if (source != source()) {
            throw new IllegalArgumentException("Source state incompatible");
        } else {
            return target();
        }
    }

    /**
     * This implementation reconstructs the rule application from the stored
     * footprint, and appends an isomorphism to the actual target if necessary.
     */
    public Morphism getMorphism() {
        if (this.morphism == null) {
            this.morphism = computeMorphism();
        }
        return this.morphism;
    }

    /**
     * Constructs an underlying morphism for the transition from the stored
     * footprint.
     */
    protected Morphism computeMorphism() {
        RuleApplication appl = getEvent().newApplication(source().getGraph());
        if (isSymmetry()) {
            Graph derivedTarget = new NodeSetEdgeSetGraph(appl.getTarget());
            Graph realTarget = new NodeSetEdgeSetGraph(target().getGraph());
            Morphism iso = derivedTarget.getIsomorphismTo(realTarget);
            assert iso != null : "Can't reconstruct derivation from graph transition "
                + this
                + ": \n"
                + AbstractGraphShape.toString(derivedTarget)
                + " and \n"
                + AbstractGraphShape.toString(realTarget)
                + " \nnot isomorphic";
            return appl.getMorphism().then(iso);
        } else {
            return appl.getMorphism();
        }
    }

    // ----------------------- OBJECT OVERRIDES -----------------------

    /**
     * This implementation compares objects on the basis of the source graph,
     * rule and anchor images.
     */
    protected boolean equalsSource(GraphTransition other) {
        return source() == other.source();
    }

    /**
     * This implementation compares objects on the basis of the source graph,
     * rule and anchor images.
     */
    protected boolean equalsEvent(GraphTransition other) {
        return getEvent().equals(other.getEvent());
    }

    /**
     * This implementation delegates to
     * <tt>{@link #equalsSource(GraphTransition)}</tt>.
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof GraphTransition
            && equalsSource((GraphTransition) obj)
            && equalsEvent((GraphTransition) obj);
    }

    /**
     * This implementation combines the hash codes of the rule and the anchor
     * images.
     */
    @Override
    protected int computeHashCode() {
        return System.identityHashCode(this.source)
            + System.identityHashCode(this.event);
    }

    @Override
    public int compareTo(Element obj) {
        if (obj instanceof GraphTransition) {
            GraphTransition other = (GraphTransition) obj;
            int result = source().compareTo(other.source());
            if (result == 0) {
                result = getEvent().compareTo(other.getEvent());
                if (result == 0) {
                    result = target().compareTo(other.target());
                }
            }
            return result;
        } else {
            assert obj instanceof GraphState : String.format(
                "Can't compare graph transition %s to element %s", this, obj);
            int result = source().compareTo(obj);
            if (result == 0) {
                result = +1;
            }
            return result;
        }
    }

    /**
     * The underlying rule of this transition.
     * @invariant <tt>rule != null</tt>
     */
    protected RuleEvent event;
    /** The array of added nodes of this transition. */
    private final Node[] addedNodes;
    /**
     * The underlying morphism of this transition. Computed lazily (using the
     * footprint) using {@link #computeMorphism()}.
     */
    private Morphism morphism;
    /** Flag indicating that the underlying morphism is a partial identity. */
    private final boolean symmetry;

    /** Returns the total number of anchor images created. */
    static public int getAnchorImageCount() {
        return anchorImageCount;
    }

    /** The total number of anchor images created. */
    static private int anchorImageCount = 0;

    /**
     * Returns the ControlTransition with which this transition is associated
     * @return the ControlTransition with which this transition is associated, 
     * or null if no control is present
     */
    public ControlTransition getControlTransition() {
        if (this.source.getLocation() != null) {
            return ((ControlState) this.source.getLocation()).getTransition(this.event.getRule());
        } else {
            return null;
        }
    }

    /**
     * Returns whether this transition is a self transition (graph does not change,
     * ControlLocation does not change, no output parameters)
     * @return whether this transition is a self transition
     */
    public boolean isSelfTransition() {
        boolean retval = !this.event.getRule().isModifying();
        if (retval && this.source.getLocation() != null) {
            retval &=
                this.source.getLocation() == this.getControlTransition().target();
            retval &= this.getControlTransition().hasOutputParameters();
        }
        return retval;
    }
}