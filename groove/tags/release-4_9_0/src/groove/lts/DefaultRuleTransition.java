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

import groove.control.CtrlTransition;
import groove.grammar.Recipe;
import groove.grammar.Rule;
import groove.grammar.host.HostEdge;
import groove.grammar.host.HostGraph;
import groove.grammar.host.HostGraphMorphism;
import groove.grammar.host.HostNode;
import groove.grammar.model.FormatException;
import groove.graph.AEdge;
import groove.graph.AGraph;
import groove.graph.EdgeRole;
import groove.graph.Morphism;
import groove.graph.iso.IsoChecker;
import groove.transform.AbstractRuleEvent;
import groove.transform.Proof;
import groove.transform.RuleApplication;
import groove.transform.RuleEvent;

import java.util.Collections;

/**
 * Models a transition built upon a rule application
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-03-05 16:50:10 $
 */
public class DefaultRuleTransition extends
        AEdge<GraphState,RuleTransitionLabel> implements
        RuleTransitionStub, RuleTransition {
    /**
     * Constructs a GraphTransition on the basis of a given rule event, between
     * a given source and target state.
     */
    public DefaultRuleTransition(GraphState source, MatchResult match,
            HostNode[] addedNodes, GraphState target, boolean symmetry) {
        super(source,
            RuleTransitionLabel.createLabel(source, match, addedNodes), target);
        this.symmetry = symmetry;
    }

    /**
     * @param source the source state
     * @param match the rule event
     * @param target the target state
     */
    public DefaultRuleTransition(GraphState source, MatchResult match,
            GraphState target) {
        this(source, match, null, target, false);
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
    public RuleTransition getInitial() {
        return this;
    }

    @Override
    public Iterable<RuleTransition> getSteps() {
        return Collections.<RuleTransition>singletonList(this);
    }

    @Override
    public String getOutputString() throws FormatException {
        return ((AbstractRuleEvent<?,?>) getEvent()).getOutputString(getAddedNodes());
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

    @Override
    public MatchResult getKey() {
        return new MatchResult(this);
    }

    public RuleTransitionStub toStub() {
        if (isSymmetry()) {
            return new SymmetryTransitionStub(getKey(), getAddedNodes(),
                target());
        } else if (target() instanceof DefaultGraphNextState) {
            return ((DefaultGraphNextState) target()).createInTransitionStub(
                source(), getKey(), getAddedNodes());
        } else {
            return new IdentityTransitionStub(getKey(), getAddedNodes(),
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
    public GraphTransitionKey getKey(GraphState source) {
        if (source != source()) {
            throw new IllegalArgumentException("Source state incompatible");
        } else {
            return getKey();
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
        HostGraphMorphism result;
        HostGraph sourceGraph = source().getGraph();
        if (getAction().isModifying()) {
            RuleApplication appl = getEvent().newApplication(sourceGraph);
            result = appl.getMorphism();
            if (isSymmetry()) {
                HostGraph derivedTarget = appl.getTarget().clone();
                HostGraph realTarget = target().getGraph().clone();
                final Morphism<HostNode,HostEdge> iso =
                    IsoChecker.getInstance(true).getIsomorphism(derivedTarget,
                        realTarget);
                assert iso != null : "Can't reconstruct derivation from graph transition "
                    + this
                    + ": \n"
                    + AGraph.toString(derivedTarget)
                    + " and \n"
                    + AGraph.toString(realTarget)
                    + " \nnot isomorphic";
                result = result.then(iso);
            }
        } else {
            // create an identity morphism
            result = sourceGraph.getFactory().createMorphism();
            for (HostNode node : sourceGraph.nodeSet()) {
                result.putNode(node, node);
            }
            for (HostEdge edge : sourceGraph.edgeSet()) {
                result.putEdge(edge, edge);
            }
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

    /** Returns the (possibly {@code null} underlying control transition. */
    public CtrlTransition getCtrlTransition() {
        return this.label.getCtrlTransition();
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