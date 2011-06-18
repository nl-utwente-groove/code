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

import groove.control.CtrlState;
import groove.control.CtrlTransition;
import groove.graph.AbstractEdge;
import groove.graph.AbstractGraph;
import groove.graph.EdgeRole;
import groove.graph.Element;
import groove.graph.Morphism;
import groove.graph.iso.IsoChecker;
import groove.trans.AbstractEvent;
import groove.trans.DefaultHostGraph;
import groove.trans.HostEdge;
import groove.trans.HostGraph;
import groove.trans.HostGraphMorphism;
import groove.trans.HostNode;
import groove.trans.Proof;
import groove.trans.RuleApplication;
import groove.trans.RuleEvent;
import groove.view.FormatException;

/**
 * Models a transition built upon a rule application
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-03-05 16:50:10 $
 */
public class DefaultGraphTransition extends
        AbstractEdge<GraphState,DerivationLabel> implements
        GraphTransitionStub, GraphTransition {
    /**
     * Constructs a GraphTransition on the basis of a given rule event, between
     * a given source and target state.
     */
    public DefaultGraphTransition(RuleEvent event, HostNode[] addedNodes,
            GraphState source, GraphState target, boolean symmetry) {
        super(source, new DerivationLabel(event, addedNodes), target);
        this.event = event;
        this.addedNodes = addedNodes;
        this.symmetry = symmetry;
        CtrlState sourceCtrl = source.getCtrlState();
        this.ctrlTrans =
            sourceCtrl == null ? null
                    : sourceCtrl.getTransition(event.getRule());
    }

    /**
     * @param event the rule event
     * @param source the source state
     * @param target the target state
     */
    public DefaultGraphTransition(RuleEvent event, GraphState source,
            GraphState target) {
        this(event, null, source, target, false);
    }

    public RuleEvent getEvent() {
        return this.event;
    }

    @Override
    public String getOutputString() throws FormatException {
        return ((AbstractEvent<?,?>) getEvent()).getOutputString(getAddedNodes());
    }

    public boolean isSymmetry() {
        return this.symmetry;
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

    public HostNode[] getAddedNodes() {
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

    public Proof getMatch() {
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
    public HostNode[] getAddedNodes(GraphState source) {
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
    public HostGraphMorphism getMorphism() {
        if (this.morphism == null) {
            this.morphism = computeMorphism();
        }
        return this.morphism;
    }

    /** Callback method to construct a rule application from this
     * state, considered as a graph transition.
     */
    public RuleApplication createRuleApplication() {
        return new RuleApplication(getEvent(), source().getGraph(),
            target().getGraph(), getAddedNodes());
    }

    /**
     * Constructs an underlying morphism for the transition from the stored
     * footprint.
     */
    protected HostGraphMorphism computeMorphism() {
        RuleApplication appl = getEvent().newApplication(source().getGraph());
        if (isSymmetry()) {
            HostGraph derivedTarget = new DefaultHostGraph(appl.getTarget());
            HostGraph realTarget = new DefaultHostGraph(target().getGraph());
            final Morphism<HostNode,HostEdge> iso =
                IsoChecker.<HostNode,HostEdge>getInstance(true).getIsomorphism(
                    derivedTarget, realTarget);
            assert iso != null : "Can't reconstruct derivation from graph transition "
                + iso
                + ": \n"
                + AbstractGraph.toString(derivedTarget)
                + " and \n"
                + AbstractGraph.toString(realTarget)
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

    /** Returns the (possibly {@code null} underlying control transition. */
    public CtrlTransition getCtrlTransition() {
        return this.ctrlTrans;
    }

    private final CtrlTransition ctrlTrans;

    /**
     * The underlying rule of this transition.
     * @invariant <tt>rule != null</tt>
     */
    private RuleEvent event;
    /** The array of added nodes of this transition. */
    private final HostNode[] addedNodes;
    /**
     * The underlying morphism of this transition. Computed lazily (using the
     * footprint) using {@link #computeMorphism()}.
     */
    private HostGraphMorphism morphism;
    /** Flag indicating that the underlying morphism is a partial identity. */
    private final boolean symmetry;

    /** Returns the total number of anchor images created. */
    static public int getAnchorImageCount() {
        return anchorImageCount;
    }

    /** The total number of anchor images created. */
    static private int anchorImageCount = 0;

}