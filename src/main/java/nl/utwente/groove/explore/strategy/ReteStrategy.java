/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package nl.utwente.groove.explore.strategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

import nl.utwente.groove.explore.result.Acceptor;
import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.lts.DefaultGraphNextState;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GTSListener;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.MatchResult;
import nl.utwente.groove.match.MatcherFactory;
import nl.utwente.groove.match.SearchEngine;
import nl.utwente.groove.match.rete.ReteSearchEngine;
import nl.utwente.groove.transform.DeltaStore;
import nl.utwente.groove.util.Reporter;

/**
 * @author Amir Hossein Ghamarian
 * @version $Revision$
 */
public class ReteStrategy extends GTSStrategy {
    @Override
    protected void prepare(GTS gts, GraphState state, Acceptor acceptor) {
        gts.getRecord()
            .setCopyGraphs(false);
        super.prepare(gts, state, acceptor);
        gts.addLTSListener(this.exploreListener);
        clearPool();
        this.newStates.clear();
        // initialise the rete network
        this.rete = new ReteSearchEngine(gts.getGrammar());
        this.oldEngine = MatcherFactory.instance(gts.isSimple())
            .getEngine();
        MatcherFactory.instance(gts.isSimple())
            .setEngine(this.rete);
        //this.rete.getNetwork().save("e:\\temp\\reg-exp.gst", "reg-exp");
    }

    @Override
    public GraphState doNext() throws InterruptedException {
        GraphState state = getNextState();

        if (DEBUG) {
            System.out.printf("Exploring state %s%n", state);
        }
        ReteStrategyNextReporter.start();
        Collection<? extends MatchResult> ruleMatches = state.getMatches();
        for (MatchResult nextMatch : ruleMatches) {
            if (DEBUG) {
                System.out.printf("  Exploring match %s%n", nextMatch);
            }
            state.applyMatch(nextMatch);
        }

        addToPool();
        setNextState();
        ReteStrategyNextReporter.stop();
        return state;
    }

    @Override
    public void finish() {
        super.finish();
        MatcherFactory.instance(getGTS().isSimple())
            .setEngine(this.oldEngine);
        getGTS().removeLTSListener(this.exploreListener);
    }

    @Override
    protected GraphState computeNextState() {
        DefaultGraphNextState result = topOfPool();
        if (result == null) {
            // nothing left to be done
            return result;
        }
        HostGraph reteGraph = this.rete.getNetwork()
            .getState()
            .getHostGraph();
        boolean reteIsConsistent = reteGraph == getNextState().getGraph();
        DeltaStore delta = new DeltaStore();
        if (getNextState() == result) {
            DefaultGraphNextState triedState;
            // no transitions were generated since the last attempt
            // so we backtrack until the first forward step
            do {
                if (reteIsConsistent) {
                    result.getDelta()
                        .applyDelta(delta);
                }
                triedState = result;
                popPool();
                result = topOfPool();
                if (result == null) {
                    // we've backtracked all the way to the top;
                    // nothing left to be done
                    return result;
                }
            } while (result.source() != triedState.source());
            delta = delta.invert();
        }
        if (!reteIsConsistent) {
            this.rete.graphChanged(result.source()
                .getGraph());
        }
        result.getDelta()
            .applyDelta(delta);
        this.rete.transitionOccurred(result.getGraph(), delta);
        return result;
    }

    private void addToPool() {
        for (DefaultGraphNextState newState : this.newStates) {
            this.stack.push(newState);
        }
        this.newStates.clear();
    }

    /** Returns the next element from the pool of explorable states. */
    protected void popPool() {
        if (!this.stack.isEmpty()) {
            this.stack.pop();
        }
    }

    /** Returns the next element from the pool of explorable states. */
    protected DefaultGraphNextState topOfPool() {
        if (this.stack.isEmpty()) {
            return null;
        } else {
            return this.stack.peek();
        }
    }

    /** Clears the pool, in order to prepare the strategy for reuse. */
    protected void clearPool() {
        this.stack.clear();
    }

    /** Internal store of newly generated states. */
    private final Collection<DefaultGraphNextState> newStates = new ArrayList<>();

    /** Listener to keep track of states added to the GTS. */
    private final ExploreListener exploreListener = new ExploreListener();

    /** A queue with states to be explored, used as a FIFO. */
    private class ExploreListener implements GTSListener {
        @Override
        public void addUpdate(GTS gts, GraphState state) {
            if (!state.isClosed()) {
                ReteStrategy.this.newStates.add((DefaultGraphNextState) state);
            }
        }
    }

    private final Stack<DefaultGraphNextState> stack = new Stack<>();

    private ReteSearchEngine rete;

    private SearchEngine oldEngine;

    static private final boolean DEBUG = false;

    /**
     * The reporter object
     */
    static public final Reporter reporter = Reporter.register(ReteStrategy.class);
    /** Handle for profiling {@link #doNext()}. */
    static public final Reporter ReteStrategyNextReporter = reporter.register("ReteOptimized()");

}
