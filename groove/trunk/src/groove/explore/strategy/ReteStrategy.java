/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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

import groove.lts.DefaultGraphNextState;
import groove.lts.GTS;
import groove.lts.GTSAdapter;
import groove.lts.GraphState;
import groove.lts.MatchResult;
import groove.lts.RuleTransition;
import groove.match.MatcherFactory;
import groove.match.SearchEngine;
import groove.match.rete.ReteSearchEngine;
import groove.trans.DeltaStore;
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
        if (getState() == null) {
            unprepare();
            getGTS().removeLTSListener(this.exploreListener);
            ReteStrategyNextReporter.stop();
            return false;
        }

        Collection<? extends MatchResult> ruleMatches =
            getState().getAllMatches();
        Collection<GraphState> outTransitions =
            new ArrayList<GraphState>(ruleMatches.size());

        for (MatchResult nextMatch : ruleMatches) {
            RuleTransition trans = getState().applyMatch(nextMatch);
            outTransitions.add(trans.target());
        }

        addToPool(outTransitions);
        getState().setClosed(true);
        this.deltaAccumulator = new DeltaStore();
        updateAtState();
        ReteStrategyNextReporter.stop();
        return true;
    }

    @Override
    public void prepare(GTS gts, GraphState startState) {
        super.prepare(gts, startState);
        gts.getRecord().setCopyGraphs(false);
        getGTS().addLTSListener(this.exploreListener);
        clearPool();
        this.newStates.clear();
        //initializing the rete network
        this.rete = new ReteSearchEngine(gts.getGrammar());
        this.oldEngine = MatcherFactory.instance().getEngine();
        MatcherFactory.instance().setEngine(this.rete);
        //this.rete.getNetwork().save("e:\\temp\\reg-exp.gst", "reg-exp");
    }

    /**
     * Does some clean-up for when the full exploration is finished.
     */
    protected void unprepare() {
        MatcherFactory.instance().setEngine(this.oldEngine);
    }

    @Override
    protected GraphState getNextState() {
        GraphState result = topOfPool();
        GraphState triedState = null;
        if (result == null) {
            return result;
        }
        if (getState() == result) {
            do {
                ((DefaultGraphNextState) result).getDelta().applyDelta(
                    this.deltaAccumulator);
                triedState = result;
                popPool();
                result = topOfPool();
                if (result == null) {
                    return result;
                }
            } while (((DefaultGraphNextState) result).source() != ((DefaultGraphNextState) triedState).source());
        }
        this.deltaAccumulator = this.deltaAccumulator.invert();
        ((DefaultGraphNextState) result).getDelta().applyDelta(
            this.deltaAccumulator);
        this.rete.transitionOccurred(result.getGraph(), this.deltaAccumulator);
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

    /** Internal store of newly generated states. */
    private final Collection<GraphState> newStates =
        new ArrayList<GraphState>();

    /** Listener to keep track of states added to the GTS. */
    private final ExploreListener exploreListener = new ExploreListener();

    /** A queue with states to be explored, used as a FIFO. */
    private class ExploreListener extends GTSAdapter {
        @Override
        public void addUpdate(GTS gts, GraphState state) {
            if (!state.isClosed()) {
                ReteStrategy.this.newStates.add(state);
            }
        }
    }

    private final Stack<GraphState> stack = new Stack<GraphState>();

    private ReteSearchEngine rete;

    private SearchEngine oldEngine;

    /**
     * The reporter object
     */
    static public final Reporter reporter =
        Reporter.register(ReteStrategy.class);
    /** Handle for profiling {@link #next()}. */
    static public final Reporter ReteStrategyNextReporter =
        reporter.register("ReteOptimized()");

}
