// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2023 University of Twente

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
 * $Id$
 */
package nl.utwente.groove.lts;

import java.util.Collections;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.control.instance.Step;
import nl.utwente.groove.grammar.host.HostEdge;
import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.grammar.host.HostGraphMorphism;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.graph.AEdge;
import nl.utwente.groove.graph.AGraph;
import nl.utwente.groove.graph.Morphism;
import nl.utwente.groove.graph.iso.IsoChecker;
import nl.utwente.groove.transform.RuleApplication;
import nl.utwente.groove.transform.RuleEvent;
import nl.utwente.groove.util.Exceptions;

/**
 * Models a transition built upon a rule application
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-03-05 16:50:10 $
 */
@NonNullByDefault
public class DefaultRuleTransition extends AEdge<GraphState,RuleTransitionLabel>
    implements RuleTransitionStub, RuleTransition {
    /**
     * Constructs a GraphTransition on the basis of a given match and added node set, between
     * a given source and target state.
     */
    public DefaultRuleTransition(GraphState source, MatchResult match,
                                 @NonNull HostNode[] addedNodes, GraphState target,
                                 boolean symmetry) {
        super(source, RuleTransitionLabel.createLabel(source, match, addedNodes), target);
        this.symmetry = symmetry;
    }

    @Override
    public boolean isSimple() {
        return true;
    }

    @Override
    public RuleEvent getEvent() {
        return label().getEvent();
    }

    @Override
    public Iterable<RuleTransition> getSteps() {
        return Collections.<RuleTransition>singletonList(this);
    }

    @Override
    public boolean isSymmetry() {
        return this.symmetry;
    }

    @Override
    public HostNode[] getAddedNodes() {
        return label().getAddedNodes();
    }

    @Override
    public RuleTransitionStub toStub() {
        if (isSymmetry()) {
            return new SymmetryTransitionStub(getKey(), getAddedNodes(), target());
        } else if (target() instanceof DefaultGraphNextState dgns) {
            return dgns.createInTransitionStub(source(), getKey(), getAddedNodes());
        } else {
            return new IdentityTransitionStub(getKey(), getAddedNodes(), target());
        }
    }

    /**
     * This implementation throws an {@link IllegalArgumentException} if
     * <code>source</code> is not equal to the source of the transition,
     * otherwise it returns {@link #getEvent()}.
     */
    @Override
    public GraphTransitionKey getKey(GraphState source) {
        if (source != source()) {
            throw Exceptions
                .illegalArg("Source state %s should coincide with argument %s", source(), source);
        } else {
            return getKey();
        }
    }

    /**
     * This implementation throws an {@link IllegalArgumentException} if
     * <code>source</code> is not equal to the source of the transition,
     * otherwise it returns {@link #getAddedNodes()}.
     */
    @Override
    public HostNode[] getAddedNodes(GraphState source) {
        if (source != source()) {
            throw Exceptions
                .illegalArg("Source state %s should coincide with argument %s", source(), source);
        } else {
            return getAddedNodes();
        }
    }

    /**
     * This implementation throws an {@link IllegalArgumentException} if
     * <code>source</code> is not equal to the source of the transition,
     * otherwise it returns <code>this</code>.
     */
    @Override
    public RuleTransition toTransition(GraphState source) {
        if (source != source()) {
            throw Exceptions
                .illegalArg("Source state %s should coincide with argument %s", source(), source);
        } else {
            return this;
        }
    }

    /**
     * This implementation throws an {@link IllegalArgumentException} if
     * <code>source</code> is not equal to the source of the transition,
     * otherwise it returns <code>target()</code>.
     */
    @Override
    public GraphState getTarget(GraphState source) {
        if (source != source()) {
            throw Exceptions
                .illegalArg("Source state %s should coincide with argument %s", source(), source);
        } else {
            return target();
        }
    }

    /**
     * This implementation reconstructs the rule application from the stored
     * footprint, and appends an isomorphism to the actual target if necessary.
     */
    @Override
    public HostGraphMorphism getMorphism() {
        var result = this.morphism;
        if (result == null) {
            this.morphism = result = computeMorphism();
        }
        return result;
    }

    /**
     * The underlying morphism of this transition. Computed lazily (using the
     * footprint) using {@link #computeMorphism()}.
     */
    private @Nullable HostGraphMorphism morphism;

    /**
     * Constructs an underlying morphism for the transition from the stored
     * footprint.
     */
    protected HostGraphMorphism computeMorphism() {
        HostGraphMorphism result;
        HostGraph sourceGraph = source().getGraph();
        if (getAction().isModifying()) {
            // create fresh rule application to account for target isomorphism
            RuleApplication appl = new RuleApplication(getEvent(), sourceGraph, getAddedNodes());
            result = appl.getMorphism();
            if (isSymmetry()) {
                HostGraph derivedTarget = appl.getTarget().clone();
                HostGraph realTarget = target().getGraph().clone();
                final Morphism<HostNode,HostEdge> iso
                    = IsoChecker.getInstance(true).getIsomorphism(derivedTarget, realTarget);
                assert iso != null : "Can't reconstruct derivation from graph transition " + this
                    + ": \n" + AGraph.toString(derivedTarget) + " and \n"
                    + AGraph.toString(realTarget) + " \nnot isomorphic";
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

    /** Callback method to construct a rule application from this
     * state, considered as a graph transition.
     */
    @Override
    public RuleApplication createRuleApplication() {
        return new RuleApplication(getEvent(), source().getGraph(), target().getGraph(),
            getAddedNodes());
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
    public boolean equals(@Nullable Object obj) {
        return obj instanceof RuleTransition rt && equalsSource(rt) && equalsEvent(rt);
    }

    /*
     * This implementation combines the hash codes of the rule and the anchor
     * images.
     */
    @Override
    protected int computeHashCode() {
        return System.identityHashCode(source()) + System.identityHashCode(getEvent());
    }

    @Override
    public Step getStep() {
        return label().getStep();
    }

    /** Flag indicating that the underlying morphism is a partial identity. */
    private final boolean symmetry;

    /** Returns the total number of anchor images created. */
    static public int getAnchorImageCount() {
        return anchorImageCount;
    }

    /** The total number of anchor images created. */
    static private int anchorImageCount = 0;
}