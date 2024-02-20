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
 *
 * $Id$
 */
package nl.utwente.groove.lts;

import java.util.Arrays;

import org.eclipse.jdt.annotation.NonNull;

import nl.utwente.groove.control.instance.Step;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.graph.Element;
import nl.utwente.groove.transform.RuleEvent;

/**
 * Abstract graph transition stub that only stores an event and a target state.
 * There are two specialisations: one that is based on an identity morphism ({@link SymmetryTransitionStub})
 * and one that is not ({@link SymmetryTransitionStub}). The only abstract
 * method is {@link #toTransition(GraphState)}.
 * @author Arend Rensink
 * @version $Revision$
 */
abstract class AbstractRuleTransitionStub implements RuleTransitionStub {
    /**
     * Constructs a stub on the basis of a given rule event, added nodes and
     * target state.
     */
    AbstractRuleTransitionStub(MatchResult match, HostNode[] addedNodes, GraphState target) {
        this.event = match.getEvent();
        this.step = match.getStep();
        this.addedNodes = addedNodes;
        this.target = target;
    }

    /** This implementation always returns the stored target state. */
    @Override
    public GraphState getTarget(GraphState source) {
        return this.target;
    }

    /** The event wrapped by this stub. */
    @Override
    public final RuleEvent getEvent() {
        return this.event;
    }

    /**
     * The rule event of this transition stub.
     */
    private final RuleEvent event;

    @Override
    public final Rule getAction() {
        return getEvent().getRule();
    }

    @Override
    public Step getStep() {
        return this.step;
    }

    /**
     * The control transition of this transition stub.
     */
    private final Step step;

    @Override
    public @NonNull MatchResult getKey() {
        return new MatchResult(getEvent(), getStep());
    }

    @Override
    public MatchResult getKey(GraphState source) {
        return getKey();
    }

    @Override
    public HostNode[] getAddedNodes(GraphState source) {
        return this.addedNodes;
    }

    /**
     * The added nodes of this transition stub.
     */
    private final HostNode[] addedNodes;

    /**
     * This implementation compares events for identity.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else {
            return obj instanceof AbstractRuleTransitionStub stub && equalsStub(stub);
        }
    }

    @Override
    public RuleTransition toTransition(GraphState source) {
        return new DefaultRuleTransition(source, getKey(source), getAddedNodes(source),
            getTarget(source), isSymmetry());
    }

    /**
     * Compares the events of this and the other transition. Callback method
     * from {@link #equals(Object)}.
     */
    protected boolean equalsStub(AbstractRuleTransitionStub other) {
        boolean result = this.target == other.target && getEvent() == other.getEvent()
            && isSymmetry() == other.isSymmetry();
        assert !result || Arrays.equals(this.addedNodes, other.addedNodes);
        return result;
    }

    /**
     * This method is only there because we needed to make
     * {@link nl.utwente.groove.lts.RuleTransitionStub} a sub-interface of {@link Element}.
     * The method throws an {@link UnsupportedOperationException} always.
     */
    public int compareTo(Element obj) {
        throw new UnsupportedOperationException();
    }

    /**
     * This implementation returns the identity of the event.
     */
    @Override
    public int hashCode() {
        return getEvent().hashCode() + this.target.hashCode();
    }

    /**
     * The target state of this transition.
     * @invariant <tt>target != null</tt>
     */
    private final GraphState target;
}