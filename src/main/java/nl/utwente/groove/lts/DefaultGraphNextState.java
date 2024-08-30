/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
package nl.utwente.groove.lts;

import java.util.Collections;
import java.util.Optional;

import org.eclipse.jdt.annotation.NonNull;

import nl.utwente.groove.control.instance.Step;
import nl.utwente.groove.grammar.host.DeltaHostGraph;
import nl.utwente.groove.grammar.host.HostGraphMorphism;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.transform.DeltaApplier;
import nl.utwente.groove.transform.RuleApplication;
import nl.utwente.groove.transform.RuleEvent;

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
        this.callStack = frameValues;
        if (DEBUG) {
            System.out.printf("Created state %s from %s:%n", this, source);
            System.out.printf("  Graph: %s%n", source.getGraph());
            System.out.printf("  Event: %s%n", this.event.toString());
            System.out.printf("  Event id: %s%n", System.identityHashCode(match));
        }
    }

    @Override
    public RuleEvent getEvent() {
        return this.event;
    }

    @Override
    public Iterable<RuleTransition> getSteps() {
        return Collections.<RuleTransition>singletonList(this);
    }

    @Override
    public HostNode[] getAddedNodes() {
        return this.addedNodes;
    }

    @Override
    public Object[] getPrimeStack() {
        return this.callStack;
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
    public @NonNull GraphTransitionKey getKey(GraphState source) {
        if (source == source()) {
            return getKey();
        } else {
            // we are acting as a transition stub aliasing the source state
            // (interpreted as a transition)
            assert getSourceKey().isPresent();
            return getSourceKey().get();
        }
    }

    @Override
    public @NonNull HostNode[] getAddedNodes(GraphState source) {
        if (source == source()) {
            return getAddedNodes();
        } else {
            // we are acting as a transition stub aliasing the source state
            // (interpreted as a transition)
            assert getSourceKey().isPresent();
            return getSourceAddedNodes().get();
        }
    }

    @Override
    public boolean isSymmetry() {
        return false;
    }

    @Override
    public GraphTransition getInTransition() {
        if (isInner() || !isInnerStep()) {
            return this;
        }
        // find the initial rule transition
        RuleTransition initial = this;
        while (initial.source().isInner()) {
            // recipe states cannot be the initial state, so it's a GraphNextState
            initial = (GraphNextState) initial.source();
        }
        // look for the corresponding outgoing transition
        RecipeTransition result = null;
        for (GraphTransition trans : initial.source().getTransitions()) {
            if (!(trans instanceof RecipeTransition rt)) {
                continue;
            }
            result = rt;
            if (result.target() == this && result.getInitial() == initial) {
                break;
            } else {
                result = null;
            }
        }
        return result == null
            ? this
            : result;
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
            assert !getSourceKey().isEmpty();
            assert !getSourceAddedNodes().isEmpty();
            return new DefaultRuleTransition(source, getSourceKey().get(),
                getSourceAddedNodes().get(), this, isSymmetry());
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
     * {@link nl.utwente.groove.lts.RuleTransitionStub}.
     */
    protected Optional<MatchResult> getSourceKey() {
        return source() instanceof GraphNextState gns
            ? Optional.of(gns.getKey())
            : Optional.empty();
    }

    /**
     * Returns the event from the source of this transition, if that is itself a
     * {@link nl.utwente.groove.lts.RuleTransitionStub}.
     */
    protected Optional<HostNode[]> getSourceAddedNodes() {
        return source() instanceof GraphNextState gns
            ? Optional.of(getAddedNodes())
            : Optional.empty();
    }

    /**
     * This implementation compares the state on the basis of its qualities as a
     * {@link RuleTransition}. That is, two objects are considered equal if
     * they have the same source and rule transition key.
     * @see #equalsTransition(RuleTransition)
     */
    @Override
    public boolean equals(Object obj) {
        return obj == this || (obj instanceof RuleTransition rt) && equalsTransition(rt);
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
        } else if (source != source() && getSourceKey().map(k -> k == match).orElse(false)) {
            return this;
        } else {
            return super.createInTransitionStub(source, match, addedNodes);
        }
    }

    @Override
    public Step getStep() {
        return this.step;
    }

    /** Keeps track of bound variables */
    private Object[] callStack;
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