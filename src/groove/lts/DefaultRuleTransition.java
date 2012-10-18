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
import groove.trans.HostEdge;
import groove.trans.HostGraph;
import groove.trans.HostGraphMorphism;
import groove.trans.HostNode;
import groove.trans.Proof;
import groove.trans.Recipe;
import groove.trans.Rule;
import groove.trans.RuleApplication;
import groove.trans.RuleEvent;
import groove.view.FormatException;

import java.util.Collections;

/**
 * Models a transition built upon a rule application
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-03-05 16:50:10 $
 */
public class DefaultRuleTransition extends
        AbstractEdge<GraphState,RuleTransitionLabel> implements
        RuleTransitionStub, RuleTransition {
    /**
     * Constructs a GraphTransition on the basis of a given rule event, between
     * a given source and target state.
     */
    public DefaultRuleTransition(GraphState source, RuleEvent event,
            HostNode[] addedNodes, GraphState target, boolean symmetry) {
        super(source,
            RuleTransitionLabel.createLabel(source, event, addedNodes), target);
        this.symmetry = symmetry;
    }

    /**
     * @param event the rule event
     * @param source the source state
     * @param target the target state
     */
    public DefaultRuleTransition(RuleEvent event, GraphState source,
            GraphState target) {
        this(source, event, null, target, false);
    }

    @Override
    public String text(boolean anchored) {
        return label().text(anchored);
    }

    @Override
    public Rule getAction() {
        return getEvent().getRule();
    }

    public RuleEvent getEvent() {
        return label().getEvent();
    }

    @Override
    public Iterable<RuleTransition> getSteps() {
        return Collections.<RuleTransition>singletonList(this);
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
        return label().getAddedNodes();
    }

    public RuleTransitionStub toStub() {
        if (isSymmetry()) {
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

    public Proof getProof() {
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
    public RuleTransition toTransition(GraphState source) {
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
        HostGraphMorphism result = appl.getMorphism();
        if (isSymmetry()) {
            HostGraph derivedTarget = appl.getTarget().clone();//new DefaultHostGraph(appl.getTarget());
            HostGraph realTarget = target().getGraph().clone();//new DefaultHostGraph(target().getGraph());
            final Morphism<HostNode,HostEdge> iso =
                IsoChecker.<HostNode,HostEdge>getInstance(true).getIsomorphism(
                    derivedTarget, realTarget);
            assert iso != null : "Can't reconstruct derivation from graph transition "
                + this
                + ": \n"
                + AbstractGraph.toString(derivedTarget)
                + " and \n"
                + AbstractGraph.toString(realTarget)
                + " \nnot isomorphic";
            result = result.then(iso);
        }
        return result;
    }

    // ----------------------- OBJECT OVERRIDES -----------------------

    /**
     * This implementation compares objects on the basis of the source graph,
     * rule and anchor images.
     */
    protected boolean equalsSource(RuleTransition other) {
        return source() == other.source();
    }

    /**
     * This implementation compares objects on the basis of the source graph,
     * rule and anchor images.
     */
    protected boolean equalsEvent(RuleTransition other) {
        return getEvent().equals(other.getEvent());
    }

    /**
     * This implementation delegates to
     * <tt>{@link #equalsSource(RuleTransition)}</tt>.
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof RuleTransition
            && equalsSource((RuleTransition) obj)
            && equalsEvent((RuleTransition) obj);
    }

    /**
     * This implementation combines the hash codes of the rule and the anchor
     * images.
     */
    @Override
    protected int computeHashCode() {
        return System.identityHashCode(source())
            + System.identityHashCode(getEvent());
    }

    @Override
    public int compareTo(Element obj) {
        if (obj instanceof GraphTransition) {
            GraphTransition other = (GraphTransition) obj;
            int result = source().compareTo(other.source());
            if (result == 0) {
                result =
                    getAction().getFullName().compareTo(
                        other.getAction().getFullName());
            }
            if (result == 0) {
                result =
                    getEvent().compareTo(((RuleTransition) other).getEvent());
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
        CtrlState sourceCtrl = this.source.getCtrlState();
        return sourceCtrl.getTransition(getAction());
    }

    @Override
    public boolean isPartial() {
        return getRecipe() != null;
    }

    @Override
    public Recipe getRecipe() {
        return getCtrlTransition().getRecipe();
    }

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