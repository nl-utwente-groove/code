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
 * $Id$
 */
package groove.lts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IllegalFormatException;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import groove.control.CtrlPar;
import groove.control.CtrlPar.Const;
import groove.control.CtrlPar.Var;
import groove.control.instance.Step;
import groove.control.template.Switch;
import groove.grammar.Rule;
import groove.grammar.host.HostEdge;
import groove.grammar.host.HostGraph;
import groove.grammar.host.HostGraphMorphism;
import groove.grammar.host.HostNode;
import groove.grammar.host.ValueNode;
import groove.graph.AEdge;
import groove.graph.AGraph;
import groove.graph.EdgeRole;
import groove.graph.Morphism;
import groove.graph.iso.IsoChecker;
import groove.transform.Proof;
import groove.transform.RuleApplication;
import groove.transform.RuleEvent;
import groove.util.parse.FormatException;

/**
 * Models a transition built upon a rule application
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-03-05 16:50:10 $
 */
public class DefaultRuleTransition extends AEdge<GraphState,RuleTransitionLabel>
    implements RuleTransitionStub, RuleTransition {
    /**
     * Constructs a GraphTransition on the basis of a given match and added node set, between
     * a given source and target state.
     */
    public DefaultRuleTransition(GraphState source, MatchResult match,
        @NonNull HostNode[] addedNodes, GraphState target, boolean symmetry) {
        super(source, RuleTransitionLabel.createLabel(source, match, addedNodes), target);
        this.symmetry = symmetry;
    }

    @Override
    public boolean isSimple() {
        return true;
    }

    @Override
    public String text(boolean anchored) {
        return label().text(anchored);
    }

    @Override
    public Rule getAction() {
        return getEvent().getRule();
    }

    @Override
    public RuleEvent getEvent() {
        return label().getEvent();
    }

    @Override
    public GTS getGTS() {
        return source().getGTS();
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
        return getOutputString(this);
    }

    @Override
    public boolean isSymmetry() {
        return this.symmetry;
    }

    @Override
    public EdgeRole getRole() {
        if (getAction().isModifying() || getStep().isModifying()) {
            return EdgeRole.BINARY;
        } else {
            return EdgeRole.FLAG;
        }
    }

    @Override
    public HostNode[] getAddedNodes() {
        return label().getAddedNodes();
    }

    @Override
    public List<HostNode> getArguments() {
        return getArguments(this);
    }

    @Override
    public MatchResult getKey() {
        return new MatchResult(this);
    }

    @Override
    public RuleTransitionStub toStub() {
        if (isSymmetry()) {
            return new SymmetryTransitionStub(getKey(), getAddedNodes(), target());
        } else if (target() instanceof DefaultGraphNextState) {
            return ((DefaultGraphNextState) target()).createInTransitionStub(source(),
                getKey(),
                getAddedNodes());
        } else {
            return new IdentityTransitionStub(getKey(), getAddedNodes(), target());
        }
    }

    @Override
    public Proof getProof() {
        return getEvent().getMatch(source().getGraph());
    }

    /**
     * This implementation throws an {@link IllegalArgumentException} if
     * <code>source</code> is not equal to the source of the transition,
     * otherwise it returns {@link #getEvent()}.
     */
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
    public HostGraphMorphism getMorphism() {
        if (this.morphism == null) {
            this.morphism = computeMorphism();
        }
        return this.morphism;
    }

    /**
     * The underlying morphism of this transition. Computed lazily (using the
     * footprint) using {@link #computeMorphism()}.
     */
    private HostGraphMorphism morphism;

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
                HostGraph derivedTarget = appl.getTarget()
                    .clone();
                HostGraph realTarget = target().getGraph()
                    .clone();
                final Morphism<HostNode,HostEdge> iso = IsoChecker.getInstance(true)
                    .getIsomorphism(derivedTarget, realTarget);
                assert iso != null : "Can't reconstruct derivation from graph transition " + this
                    + ": \n" + AGraph.toString(derivedTarget) + " and \n"
                    + AGraph.toString(realTarget) + " \nnot isomorphic";
                result = result.then(iso);
            }
        } else {
            // create an identity morphism
            result = sourceGraph.getFactory()
                .createMorphism();
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
    public boolean equals(Object obj) {
        return obj instanceof RuleTransition && equalsSource((RuleTransition) obj)
            && equalsEvent((RuleTransition) obj);
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

    @Override
    public Switch getSwitch() {
        return getStep().getRuleSwitch();
    }

    @Override
    public final boolean isPartial() {
        return getStep().isPartial();
    }

    @Override
    public final boolean isInternalStep() {
        return getStep().isInternal();
    }

    @Override
    public final boolean isRealStep() {
        return !isInternalStep() && source().isRealState() && target().isRealState();
    }

    /** Flag indicating that the underlying morphism is a partial identity. */
    private final boolean symmetry;

    /** Returns the total number of anchor images created. */
    static public int getAnchorImageCount() {
        return anchorImageCount;
    }

    /** The total number of anchor images created. */
    static private int anchorImageCount = 0;

    /**
     * Returns the instantiated output string for a given transition, if any.
     * @return the instantiated output string, or {@code null} if there is none
     * @throws FormatException if the format string of the rule
     * does not correspond to the actual rule parameters.
     */
    public static String getOutputString(GraphTransition trans) throws FormatException {
        String result = null;
        String formatString = trans.getAction()
            .getFormatString();
        if (formatString != null && !formatString.isEmpty()) {
            List<Object> args = new ArrayList<>();
            for (HostNode arg : trans.label()
                .getArguments()) {
                if (arg instanceof ValueNode) {
                    args.add(((ValueNode) arg).getValue());
                } else {
                    args.add(arg.toString());
                }
            }
            try {
                result = String.format(formatString, args.toArray());
            } catch (IllegalFormatException e) {
                throw new FormatException("Error in rule output string: %s", e.getMessage());
            }
        }
        return result;
    }

    /** Computes the list of call arguments for a given graph transition. */
    public static List<HostNode> getArguments(GraphTransition trans) {
        List<HostNode> result;
        List<? extends CtrlPar> args = trans.getSwitch()
            .getArgs();
        if (args.isEmpty()) {
            result = EMPTY_ARGS;
        } else {
            result = new ArrayList<>();
            for (int i = 0; i < args.size(); i++) {
                CtrlPar par = args.get(i);
                if (par instanceof Var) {
                    Var var = (Var) par;
                    if (var.isInOnly()) {
                        // look up value in source state
                    } else {
                        assert var.isOutOnly();
                        // look up value in target state
                    }
                } else {
                    assert par instanceof Const;
                    result.add(((Const) par).getNode());
                }
            }
        }
        return result;
    }

    private static final List<HostNode> EMPTY_ARGS = Collections.emptyList();
}