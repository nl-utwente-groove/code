/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2010
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
package groove.explore.strategy;

import groove.explore.util.MatchSetCollector;
import groove.graph.DeltaStore;
import groove.lts.DefaultGraphNextState;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.LTS;
import groove.lts.LTSAdapter;
import groove.lts.MatchResult;
import groove.match.SearchEngineFactory;
import groove.match.SearchEngineFactory.EngineType;
import groove.match.rete.ReteSearchEngine;
import groove.trans.Rule;
import groove.trans.RuleMatch;
import groove.trans.SystemRecord;
import groove.util.Reporter;

/**
 * Explores a single path until reaching a final state or a loop. In case of
 * abstract simulation, this implementation will prefer going along a path then
 * stopping exploration when a loop is met.
 * @author Amir Hossein Ghamarian
 * 
 */
public class ReteLinearStrategy extends AbstractStrategy {
    /**
     * Constructs a default instance of the strategy, in which states are only
     * closed if they have been fully explored
     */
    public ReteLinearStrategy() {
        this(false);
    }

    /**
     * Constructs an instance of the strategy with control over the closing of
     * states.
     * @param closeFast if <code>true</code>, close states immediately after a
     *        single outgoing transition has been computed.
     */
    public ReteLinearStrategy(boolean closeFast) {
        if (closeFast) {
            enableCloseExit();
        }
    }

    private void enableCloseExit() {
        this.closeExit = true;
    }

    @Override
    public boolean next() {
        if (this.atState == null) {
            getGTS().removeLTSListener(this.collector);
            unprepare();
            return false;
        }

        MatchResult event = getMatch();
        if (event != null) {
            getMatchApplier().apply(getAtState(), event);
            if (closeExit()) {
                setClosed(getAtState(), false);
            }
        } else {
            setClosed(getAtState(), true);
        }
        updateAtState();
        return true;
    }

    /** Callback method to return the single next match. */
    protected MatchResult getMatch() {
        return createMatchCollector().getMatch();
    }

    @Override
    protected boolean updateAtState() {
        boolean result = (this.atState = this.collector.getNewState()) != null;
        this.collector.reset();
        DeltaStore d = new DeltaStore();
        if (result) {
            ((DefaultGraphNextState) this.atState).getDelta().applyDelta(d);
            this.rete.transitionOccurred(this.atState.getGraph(), d);

        } else {
            getGTS().removeLTSListener(this.collector);
        }
        return result;
    }

    @Override
    public void prepare(GTS gts, GraphState state) {
        // We have to set the non-collapsing property before the first (start)
        // state is generated, otherwise it is too late.
        gts.getRecord().setCollapse(false);
        gts.getRecord().setCopyGraphs(false);
        gts.getRecord().setReuseEvents(false);
        super.prepare(gts, state);
        gts.addLTSListener(this.collector);

        //initializing the RETE network
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

    /**
     * Does some clean-up for when the full exploration is finished.
     */
    protected void unprepare() {
        //TODO ARASH: (Talk to Arend about this)
        //If the exploration is stopped half-way through, then this method
        //will not be called. I think The AbstractStrategy should be changed so that
        //a notification is set to an strategy that it is abruptly stopped so as to
        //let the strategy do any clean-up necessary.
        ReteSearchEngine.unlock();
        SearchEngineFactory.getInstance().setCurrentEngineType(this.oldType);
    }

    @Override
    protected ReteMatchSetCollector createMatchCollector() {
        return new ReteMatchSetCollector(getAtState(), getRecord(), this.rete,
            getGTS().checkDiamonds());
    }

    /** Return the current value of the "close on exit" setting */
    public boolean closeExit() {
        return this.closeExit;
    }

    /** Collects states newly added to the GTS. */
    private final NewStateCollector collector = new NewStateCollector();

    private EngineType oldType;

    private ReteSearchEngine rete;

    /** 
     * Option to close states immediately after a transition has been generated.
     * Used to save memory by closing states ASAP.
     */
    private boolean closeExit = false;

    /**
     * Registers the first new state added to the GTS it listens to. Such an
     * object should be added as listener only to a single GTS.
     */
    static private class NewStateCollector extends LTSAdapter {
        NewStateCollector() {
            reset();
        }

        /**
         * Returns the collected new state, or null if no new state was
         * registered.
         * @return the collected new state, or null if no new state was
         *         registered since last reset operation
         */
        GraphState getNewState() {
            return this.newState;
        }

        /** Forgets collected new state. */
        void reset() {
            this.newState = null;
        }

        @Override
        public void addUpdate(LTS shape, GraphState state) {
            if (this.newState == null) {
                this.newState = state;
            }
        }

        private GraphState newState;

    }

    private static class ReteMatchSetCollector extends MatchSetCollector {
        private ReteSearchEngine reteEngine;

        protected static final Reporter reporter =
            Reporter.register(ReteMatchSetCollector.class);
        protected static final Reporter getMatchReporter =
            reporter.register("getMatch()");

        public ReteMatchSetCollector(GraphState state, SystemRecord record,
                ReteSearchEngine engine, boolean checkDiamonds) {
            super(state, record, checkDiamonds);
            this.reteEngine = engine;
        }

        /** 
         * @return The next match available at the current state
         **/
        @Override
        public MatchResult getMatch() {
            getMatchReporter.start();
            MatchResult result = null;
            Rule currentRule = firstCall().getRule();
            while (currentRule != null) {
                for (RuleMatch ruleMatch : this.reteEngine.getRuleMatches(currentRule)) {
                    // convert the match to an event
                    result = this.record.getEvent(ruleMatch);
                    if (result != null) {
                        return result;
                    }
                }
                currentRule = nextCall(false).getRule();
            }
            getMatchReporter.stop();
            return result;
        }
    }

}
