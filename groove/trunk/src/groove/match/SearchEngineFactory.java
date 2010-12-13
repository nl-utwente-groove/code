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
package groove.match;

import groove.graph.DeltaStore;
import groove.lts.DefaultGraphNextState;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.match.rete.ReteSearchEngine;
import groove.trans.HostGraph;
import groove.trans.RuleMatch;
import groove.trans.RuleName;
import groove.trans.RuleToHostMap;
import groove.view.StoredGrammarView;

/**
 * This is a factory class generating search engines. This is where
 * decision as to which search engine is to be used in groove should be made.
 *
 * @author Arash Jalali
 * @version $Revision $
 */

public class SearchEngineFactory {

    public enum EngineType {
        SEARCH_PLAN, RETE
    };

    /**
     * This global lock flag, when <code>true</code>
     * indicates that all consumers of the search engine
     * service should refrain from using any
     * search-engine-related object instances that they may
     * have cached. If it is <code>false</code> then they
     * can continue with their cached instances or simply 
     * ask the factories for an instance. 
     */
    private static boolean globalLock = false;
    private static SearchEngineFactory instance = null;
    private EngineType currentEngineType;

    private SearchEngineFactory() {
        this.currentEngineType = getDefaultEngineType();
    }

    /**
     * @return The default engine used by GROOVE
     */
    public EngineType getDefaultEngineType() {
        return EngineType.SEARCH_PLAN;
    }

    /**
     * Factory method that gives the singleton instance of this 
     * class
     * @return a reference to the singleton instance of this class
     */
    public synchronized static SearchEngineFactory getInstance() {
        if (instance == null) {
            instance = new SearchEngineFactory();
        }
        return instance;
    }

    /**
     * The factory method returning the currently used search engine
     * in GROOVE. Currently supporting the Search Plan engine and
     * the RETE engine.
     * 
     * @param injective
     * @param ignoreNeg
     * @return an 
     */
    public SearchEngine<? extends AbstractMatchStrategy<RuleToHostMap>> getEngine(
            boolean injective, boolean ignoreNeg) {
        SearchEngine<? extends AbstractMatchStrategy<RuleToHostMap>> result =
            null;
        switch (this.getCurrentEngineType()) {
        case SEARCH_PLAN:
            result = SearchPlanEngine.getInstance(injective, ignoreNeg);
            break;
        case RETE:
            result = ReteSearchEngine.getInstance(injective, ignoreNeg);
        }
        return result;

    }

    public SearchEngine<? extends AbstractMatchStrategy<RuleToHostMap>> getEngine(
            boolean injective) {
        return this.getEngine(injective, false);
    }

    public EngineType getCurrentEngineType() {
        return this.currentEngineType;
    }

    public void setCurrentEngineType(EngineType engineType) {
        this.currentEngineType = engineType;
    }

    public synchronized void applyTransitionUpdate(GraphTransition transition) {
        if (this.getCurrentEngineType() == EngineType.RETE) {
            for (int inj = 0; inj <= 1; inj++) {
                ReteSearchEngine se =
                    ((ReteSearchEngine) this.getEngine(inj == 1));
                DefaultGraphNextState target =
                    (DefaultGraphNextState) transition.target();
                DeltaStore delta = new DeltaStore(target.getDelta());
                se.transitionOccurred(target.getGraph(), delta);
            }
        }
    }

    public synchronized void setGrammarUpdate(StoredGrammarView grammar) {
    }

    public synchronized void setMatchUpdate(RuleMatch match) {
    }

    public synchronized void setRuleUpdate(RuleName name) {

    }

    public synchronized void setStateUpdate(GraphState state) {
        if (this.getCurrentEngineType() == EngineType.RETE) {
            for (int inj = 0; inj <= 1; inj++) {
                ReteSearchEngine se =
                    ((ReteSearchEngine) this.getEngine(inj == 1));
                se.changeState(state);
            }
        }
    }

    public synchronized void setTransitionUpdate(GraphTransition transition) {
    }

    public synchronized void startSimulationUpdate(GTS gts) {
    }

    /**
     * This method should be called to tell the engine factory
     * to notify the current engine (if needed) that a new
     * grammar has been loaded.
     * 
     * @param g
     */
    public synchronized void newGrammarLoaded(StoredGrammarView g) {
        if (this.getCurrentEngineType() == EngineType.RETE) {
            for (int inj = 0; inj <= 1; inj++) {
                ReteSearchEngine se =
                    ((ReteSearchEngine) this.getEngine(inj == 1));
                se.setUp(g);
            }
        }
    }

    /**
     * This method should be called to tell the engine factory
     * to notify the current engine (if needed) that a new 
     * start graph has been loaded.
     * 
     * @param g
     */
    public synchronized void newStartGraphLoad(HostGraph g) {
        if (this.getCurrentEngineType() == EngineType.RETE) {
            for (int inj = 0; inj <= 1; inj++) {
                ReteSearchEngine se =
                    ((ReteSearchEngine) this.getEngine(inj == 1));
                se.initializeState(g);
            }
        }
    }
}
