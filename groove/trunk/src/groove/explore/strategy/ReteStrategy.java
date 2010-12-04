/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.explore.strategy;

import groove.graph.DeltaStore;
import groove.graph.GraphShape;
import groove.graph.Node;
import groove.lts.DefaultGraphNextState;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.LTSAdapter;
import groove.lts.MatchResult;
import groove.match.SearchEngineFactory;
import groove.match.SearchEngineFactory.EngineType;
import groove.match.rete.ReteSearchEngine;
import groove.util.Reporter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

/**
 * @author Amir Hossein Ghamarian 
 * @version $Revision $
 */
public class ReteStrategy extends AbstractStrategy {

    @Override
    public boolean next() {
        ReteStrategyNextReporter.start();
        if (getAtState() == null) {
            unprepare();
            getGTS().removeGraphListener(this.exploreListener);
            ReteStrategyNextReporter.stop();
            return false;
        }

        Collection<MatchResult> ruleMatches =
            createMatchCollector().getMatchSet();
        Collection<GraphState> outTransitions =
            new ArrayList<GraphState>(ruleMatches.size());

        for (MatchResult nextMatch : ruleMatches) {
            GraphTransition trans =
                getMatchApplier().apply(getAtState(), nextMatch);
            outTransitions.add(trans.target());
        }

        addToPool(outTransitions);
        setClosed(getAtState(), true);
        this.deltaAccumulator = new DeltaStore();
        updateAtState();
        ReteStrategyNextReporter.stop();
        return true;
    }

    @Override
    public void prepare(GTS gts, GraphState startState) {
        super.prepare(gts, startState);
        gts.getRecord().setCopyGraphs(false);
        getGTS().addGraphListener(this.exploreListener);
        clearPool();
        this.newStates.clear();
        //initializing the rete network
        this.rete =
            groove.match.rete.ReteSearchEngine.createFreshInstance(
                gts.getGrammar().getProperties().isInjective(), false);
        this.rete.setUp(gts.getGrammar());

        this.rete.initializeState(gts.startState().getGraph());
        this.oldType = SearchEngineFactory.getInstance().getCurrentEngineType();
        SearchEngineFactory.getInstance().setCurrentEngineType(EngineType.RETE);
        ReteSearchEngine.unlock();
        boolean lockingSuccess = ReteSearchEngine.lockToInstance(this.rete);
        assert lockingSuccess;
    }

    protected void unprepare() {
        ReteSearchEngine.unlock();
        SearchEngineFactory.getInstance().setCurrentEngineType(this.oldType);
    }

    @Override
    protected boolean updateAtState() {
        GraphState next = topOfPool();
        boolean result = next != null;
        GraphState triedState = null;
        if (!result) {
            this.atState = null;
            return result;
        }
        if (this.atState != next) {
            this.atState = next;
        } else {
            do {
                ((DefaultGraphNextState) this.atState).getDelta().applyDelta(
                    this.deltaAccumulator);
                triedState = this.atState;
                popPool();
                next = topOfPool();
                if (next == null) {
                    this.atState = null;
                    return false;
                }
                this.atState = next;
            } while (((DefaultGraphNextState) this.atState).source() != ((DefaultGraphNextState) triedState).source());
        }
        this.deltaAccumulator = this.deltaAccumulator.invert();
        ((DefaultGraphNextState) this.atState).getDelta().applyDelta(
            this.deltaAccumulator);
        this.rete.transitionOccurred(this.atState.getGraph(),
            this.deltaAccumulator);
        return result;
    }

    private void addToPool(Collection<GraphState> newStates) {
        for (GraphState newState : this.newStates) {
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
    protected GraphState topOfPool() {
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

    DeltaStore deltaAccumulator;
    /** Internal store of newly generated state. */

    private EngineType oldType;

    /** Internal store of newly generated states. */
    private final Collection<GraphState> newStates =
        new ArrayList<GraphState>();

    /** Listener to keep track of states added to the GTS. */
    private final ExploreListener exploreListener = new ExploreListener();

    /** A queue with states to be explored, used as a FIFO. */
    private class ExploreListener extends LTSAdapter {
        @Override
        public void addUpdate(GraphShape graph, Node node) {
            ReteStrategy.this.newStates.add((GraphState) node);
        }
    }

    private final Stack<GraphState> stack = new Stack<GraphState>();

    private ReteSearchEngine rete;

    static public final Reporter reporter =
        Reporter.register(ReteStrategy.class);
    /** Handle for profiling {@link #next()}. */
    static public final Reporter ReteStrategyNextReporter =
        reporter.register("ReteOptimized()");

}
