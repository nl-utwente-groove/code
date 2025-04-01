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
package nl.utwente.groove.explore.strategy;

import static nl.utwente.groove.explore.strategy.ClosingStrategy.ConditionMoment.AFTER;
import static nl.utwente.groove.explore.strategy.ClosingStrategy.ConditionMoment.AT;
import static nl.utwente.groove.explore.strategy.ClosingStrategy.ConditionMoment.NONE;

import java.util.List;
import java.util.Stack;
import java.util.function.Predicate;

import nl.utwente.groove.explore.result.Acceptor;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GTSListener;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.MatchResult;
import nl.utwente.groove.lts.RuleTransition;
import nl.utwente.groove.lts.Status.Flag;

/**
 * Strategy that closes every state it explores, and adds the newly generated
 * states to a pool, together with information regarding the outgoing
 * transitions of its parent. Subclasses must decide on the order of the pool;
 * e.g., breadth-first or depth-first.
 */
abstract public class ClosingStrategy extends GTSStrategy {
    /** Instantiates a conditional closing strategy, with a given continuation condition
     * and a moment at which to apply it.
     * @param moment Moment at which to apply the condition
     * @param exploreCondition exploration continues for every state satisfying it
     */
    protected ClosingStrategy(ConditionMoment moment, Predicate<GraphState> exploreCondition) {
        this.moment = moment;
        this.exploreCondition = exploreCondition;
    }

    /** Instantiates an unconditional closing strategy. */
    protected ClosingStrategy() {
        this(NONE, (s) -> true);
    }

    @Override
    public GraphState doNext() throws InterruptedException {
        GraphState state = getNextState();
        List<MatchResult> matches = state.getMatches();
        if (state.getActualFrame().isTrial()) {
            //assert !state.isTransient();
            // there are potential rule matches now blocked until
            // the previous ones have been explored
            putBackInPool(state);
        }
        // explore known outgoing transitions of known states
        if (state.setFlag(Flag.KNOWN, false)) {
            for (RuleTransition out : state.getRuleTransitions()) {
                GraphState target = out.target();
                if (target.hasFlag(Flag.KNOWN)) {
                    addExplorable(target);
                }
            }
        }
        boolean stopAfter = isStop(AFTER, state);
        if (stopAfter) {
            setExploring(false);
        }
        for (MatchResult next : matches) {
            state.applyMatch(next);
        }
        if (stopAfter) {
            setExploring(true);
        }
        setNextState();
        return state;
    }

    @Override
    protected void prepare(GTS gts, GraphState state, Acceptor acceptor) {
        super.prepare(gts, state, acceptor);
        // for the closing strategy, there is no problem in aliasing
        // the graph data structures. On the whole, this seems wise, to
        // avoid excessive garbage collection.
        // gts.getRecord().setCopyGraphs(true);
        gts.addLTSListener(this.exploreListener);
        clearPool();
    }

    @Override
    public void finish() {
        super.finish();
        getGTS().removeLTSListener(this.exploreListener);
    }

    @Override
    protected GraphState computeNextState() {
        GraphState result;
        if (this.transientStack.isEmpty()) {
            result = getFromPool();
            while (result != null && isStop(AT, result)) {
                result = getFromPool();
            }
        } else {
            result = this.transientStack.pop();
        }
        return result;
    }

    /** Indicates if the successors of a given state should be explored.
     * This is a hook for conditional exploration.
     */
    protected boolean isStop(ConditionMoment moment, GraphState state) {
        return this.moment == moment && !this.exploreCondition.test(state);
    }

    private final ConditionMoment moment;

    private final Predicate<GraphState> exploreCondition;

    /** Adds a given state to the set of explorable states. */
    protected void addExplorable(GraphState state) {
        if (state.isTransient()) {
            ClosingStrategy.this.transientStack.push(state);
        } else {
            putInPool(state);
        }
    }

    /** Callback method to retrieve the next element from the pool.
     * @return the next element, or {@code null} when the exploration is done.
     */
    abstract protected GraphState getFromPool();

    /** Callback method to add a graph state to the pool. */
    abstract protected void putInPool(GraphState state);

    /**
     * Callback method to add a graph state back to the pool.
     * This is called instead of {@link #putInPool(GraphState)}
     * in case the rules are not scheduled all at once, so exploring
     * the state fully takes more than one go.
     */
    abstract protected void putBackInPool(GraphState state);

    /** Clears the pool, in order to prepare the strategy for reuse. */
    abstract protected void clearPool();

    /** Sets the active exploration mode.
     * @return {@code true} if the active exploration mode was changed by this call.
     */
    protected final boolean setExploring(boolean exploring) {
        boolean result = this.exploring != exploring;
        if (result) {
            this.exploring = exploring;
        }
        return result;
    }

    /** Indicates if the strategy is set to active exploration.
     * Active exploration means that states added to the GTS are added to the pool of explorables.
     */
    protected boolean isExploring() {
        return this.exploring;
    }

    private boolean exploring = true;

    /** Listener to keep track of states added to the GTS. */
    private final GTSListener exploreListener = createExploreListener();

    /**
     * Callback method to create the exploration listener.
     * The listener has the task of calling {@link #addExplorable(GraphState)} for
     * states that should be added to the frontier.
     */
    protected GTSListener createExploreListener() {
        return new GTSListener() {
            @Override
            public void addUpdate(GTS gts, GraphState state) {
                if (isExploring()) {
                    addExplorable(state);
                }
            }
        };
    }

    /** Local stack of transient states; these should be explored first. */
    private final Stack<GraphState> transientStack = new Stack<>();

    /** Setting for the condition of this strategy. */
    public enum ConditionMoment {
        /** Unconditonal: never stop. */
        NONE,
        /** Stop at a state not satisfying the condition. */
        AT,
        /** Stop after a state not satisfying the condition. */
        AFTER;
    }
}
