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
 */
package groove.lts;

import java.util.Collections;
import java.util.List;

import groove.control.instance.Step;
import groove.control.template.Switch;
import groove.grammar.Rule;
import groove.grammar.host.DeltaHostGraph;
import groove.grammar.host.HostGraphMorphism;
import groove.grammar.host.HostNode;
import groove.graph.EdgeRole;
import groove.transform.DeltaApplier;
import groove.transform.Proof;
import groove.transform.RuleApplication;
import groove.transform.RuleEvent;
import groove.util.parse.FormatException;

/**
 *
 * @author Arend
 * @version $Revision$
 */
public class DefaultGraphNextState extends AbstractGraphState
    implements GraphNextState, RuleTransitionStub {
    /**
     * Constructs a successor state on the basis of a given parent state and
     * rule application, and a given control location.
     * @param number the number of the state; required to be positive
     * @param frameValues nodes that are bound to the variables in the control frame
     */
    public DefaultGraphNextState(int number, AbstractGraphState source, MatchResult match,
        HostNode[] addedNodes, Object[] frameValues) {
        super(source.getCacheReference(), number);
        this.source = source;
        this.event = match.getEvent();
        this.addedNodes = addedNodes;
        this.step = match.getStep();
        setFrame(this.step.onFinish());
        this.frameValues = frameValues;
        if (DEBUG) {
            System.out.printf("Created state %s from %s:%n", this, source);
            System.out.printf("  Graph: %s%n", source.getGraph());
            System.out.printf("  Event: %s%n", this.event.toString());
            System.out.printf("  Event id: %s%n", System.identityHashCode(match));
        }
    }

    @Override
    public String text(boolean anchored) {
        return label().text(anchored);
    }

    @Override
    public RuleEvent getEvent() {
        return this.event;
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
        return DefaultRuleTransition.getOutputString(this);
    }

    @Override
    public HostNode[] getAddedNodes() {
        return this.addedNodes;
    }

    @Override
    public Object[] getPrimeValues() {
        return this.frameValues;
    }

    /**
     * This implementation reconstructs the matching using the rule, the anchor
     * images, and the basis graph.
     */
    @Override
    public Proof getProof() {
        return getEvent().getMatch(source().getGraph());
    }

    /**
     * Constructs an underlying morphism for the transition from the stored
     * footprint.
     */
    @Override
    public HostGraphMorphism getMorphism() {
        return createRuleApplication().getMorphism();
    }

    /** Callback method to construct a rule application from this
     * state, considered as a graph transition.
     */
    @Override
    public RuleApplication createRuleApplication() {
        return new RuleApplication(getEvent(), source().getGraph(), getGraph(), getAddedNodes());
    }

    /**
     * This implementation returns the rule name.
     */
    @Override
    public RuleTransitionLabel label() {
        return RuleTransitionLabel.createLabel(source(), getKey(), getAddedNodes());
    }

    @Override
    public EdgeRole getRole() {
        if (getAction().isModifying() || getStep().isModifying()) {
            return EdgeRole.BINARY;
        } else {
            return EdgeRole.FLAG;
        }
    }

    /**
     * Returns the basis graph of the delta graph (which is guaranteed to be a
     * {@link GraphState}).
     */
    @Override
    public AbstractGraphState source() {
        return this.source;
    }

    /**
     * This implementation returns <code>this</code>.
     */
    @Override
    public DefaultGraphNextState target() {
        return this;
    }

    @Override
    public boolean isLoop() {
        return source() == target();
    }

    @Override
    public GraphTransitionKey getKey(GraphState source) {
        if (source == source()) {
            return getKey();
        } else {
            // we are acting as a transition stub aliasing the source state
            // (interpreted as a transition)
            return getSourceKey();
        }
    }

    @Override
    public HostNode[] getAddedNodes(GraphState source) {
        if (source == source()) {
            return getAddedNodes();
        } else {
            // we are acting as a transition stub aliasing the source state
            // (interpreted as a transition)
            return getSourceAddedNodes();
        }
    }

    @Override
    public boolean isSymmetry() {
        return false;
    }

    @Override
    public GraphTransition getInTransition() {
        if (isInternalState() || !isInternalStep()) {
            return this;
        }
        // find the initial rule transition
        RuleTransition initial = this;
        while (initial.source()
            .isInternalState()) {
            // recipe states cannot be the initial state, so it's a GraphNextState
            initial = (GraphNextState) initial.source();
        }
        // look for the corresponding outgoing transition
        RecipeTransition result = null;
        for (GraphTransition trans : initial.source()
            .getTransitions()) {
            if (!(trans instanceof RecipeTransition)) {
                continue;
            }
            result = (RecipeTransition) trans;
            if (result.target() == this && result.getInitial() == initial) {
                break;
            } else {
                result = null;
            }
        }
        return result == null ? this : result;
    }

    @Override
    public MatchResult getKey() {
        return new MatchResult(this);
    }

    @Override
    public RuleTransitionStub toStub() {
        return this;
    }

    /**
     * This implementation returns a {@link DefaultRuleTransition} with
     * {@link #getSourceKey()} if <code>source</code> does not equal
     * {@link #source()}, otherwise it returns <code>this</code>.
     */
    @Override
    public RuleTransition toTransition(GraphState source) {
        if (source != source()) {
            return new DefaultRuleTransition(source, getSourceKey(), getSourceAddedNodes(), this,
                isSymmetry());
        } else {
            return this;
        }
    }

    /**
     * When a {@link DefaultGraphNextState} is used as a graph transition
     * stub, the state itself is always the target state.
     */
    @Override
    public GraphState getTarget(GraphState source) {
        return this;
    }

    @Override
    public DeltaHostGraph getGraph() {
        return getCache().getGraph();
    }

    /**
     * Returns the delta applier associated with the rule application leading up
     * to this state.
     */
    public DeltaApplier getDelta() {
        return getCache().getDelta();
    }

    /**
     * Returns the match from the source of this transition, if that is itself a
     * {@link groove.lts.RuleTransitionStub}.
     */
    protected MatchResult getSourceKey() {
        if (source() instanceof GraphNextState) {
            return ((GraphNextState) source()).getKey();
        } else {
            return null;
        }
    }

    /**
     * Returns the event from the source of this transition, if that is itself a
     * {@link groove.lts.RuleTransitionStub}.
     */
    protected HostNode[] getSourceAddedNodes() {
        if (source() instanceof GraphNextState) {
            return ((GraphNextState) source()).getAddedNodes();
        } else {
            return null;
        }
    }

    /**
     * This implementation compares the state on the basis of its qualities as a
     * {@link RuleTransition}. That is, two objects are considered equal if
     * they have the same source and rule transition key.
     * @see #equalsTransition(RuleTransition)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else {
            return (obj instanceof RuleTransition) && equalsTransition((RuleTransition) obj);
        }
    }

    /**
     * This implementation compares the rule transition key of another
     * {@link RuleTransition} to those of this object.
     */
    protected boolean equalsTransition(RuleTransition other) {
        return source() == other.source() && getEvent().equals(other.getEvent())
            && getStep().equals(other.getStep());
    }

    /**
     * This implementation combines the identities of source and event.
     */
    @Override
    public int hashCode() {
        return source().getNumber() + getEvent().hashCode() + getStep().hashCode();
    }

    /**
     * This implementation returns <code>this</code> if the derivation's event
     * is identical to the event stored in this state. Otherwise it invokes
     * <code>super</code>.
     */
    @Override
    protected RuleTransitionStub createInTransitionStub(GraphState source, MatchResult match,
        HostNode[] addedNodes) {
        if (source == source() && match.getEvent() == getEvent()) {
            return this;
        } else if (source != source() && match == getSourceKey()) {
            return this;
        } else {
            return super.createInTransitionStub(source, match, addedNodes);
        }
    }

    @Override
    public Step getStep() {
        return this.step;
    }

    @Override
    public Switch getSwitch() {
        return getStep().getRuleSwitch();
    }

    @Override
    public List<HostNode> getArguments() {
        return DefaultRuleTransition.getArguments(this);
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

    /** Keeps track of bound variables */
    private Object[] frameValues;
    /**
     * The rule of the incoming transition with which this state was created.
     */
    private final AbstractGraphState source;
    /**
     * The rule event of the incoming transition with which this state was
     * created.
     */
    private final RuleEvent event;
    /**
     * The incoming control step with which this state was
     * created.
     */
    private final Step step;
    /**
     * The identities of the nodes added with respect to the source state.
     */
    private final HostNode[] addedNodes;
    /** Flag to switch on debugging info. */
    private final static boolean DEBUG = false;
}