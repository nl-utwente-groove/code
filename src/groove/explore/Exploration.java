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
package groove.explore;

import groove.explore.result.Acceptor;
import groove.explore.result.FinalStateAcceptor;
import groove.explore.result.Result;
import groove.explore.strategy.BFSStrategy;
import groove.explore.strategy.Strategy;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.util.Reporter;

/**
 * Wrapper class that handles the execution of an exploration.
 * An exploration is given by a combination of a strategy, an acceptor and a result.
 * 
 * Implements the following public methods:
 * - prepare                - prepare stored strategy for exploration
 * - play                   - run the exploration
 * - clearResult            - clears the Result set that is stored on the acceptor
 * - isInterrupted          - checks whether the LaunchThread has been interrupted during play()
 * - getAcceptor            - returns the acceptor of the exploration
 * - getAcceptorKeyword     - returns the keyword of the acceptor
 * - getRunningTime         - returns the total running time of the exploration
 * - getResult              - returns the result of the exploration (which is stored on the acceptor)
 * - getShortName           - returns a short String identification of the exploration
 * - getStrategyKeyword     - returns the keyword of the strategy
 * - respondToGrammarChange - updates the exploration when the grammar changes
 *
 * @author Maarten de Mol
 * @version $Revision $
 */
public class Exploration {
    private Strategy strategy;
    private String strategyKeyword;
    private Acceptor acceptor;
    private String acceptorKeyword;
    private boolean interrupted;

    static private final Reporter reporter = Reporter.register(DefaultScenario.class);
    static private final int RUNNING       = reporter.newMethod("playScenario()");
   
    /**
     * Initialize an exploration by storing a strategy and an acceptor in the local
     * variables. The result is stored (set) within the acceptor. 
     * @param strategy  - strategy component of the exploration
     * @param strategyKeyword - keyword of the the strategy component
     * @param acceptor  - acceptor component of the exploration
     * @param acceptorKeyword - keyword of the acceptor component
     * @param result    - result   component of the exploration
     */
    public Exploration(Strategy strategy, String strategyKeyword,
            Acceptor acceptor, String acceptorKeyword,
            Result result) {
        this.strategy = strategy;
        this.strategyKeyword = strategyKeyword;
        this.acceptor = acceptor;
        this.acceptorKeyword = acceptorKeyword;
        this.acceptor.setResult(result);
    }
    
    /**
     * Initializes a default exploration (breadth-first, final states, infinite results).
     */
    public Exploration() {
        this(new BFSStrategy(),
             "Breadth-First",
             new FinalStateAcceptor(),
             "Final",
             new Result(0));
    }
    
    /**
     * Prepares the strategy for exploration. Can be called when no state is currently selected. 
     * @param gts - the current gts
     */
    public void prepare(GTS gts) {
        prepare(gts, null);
    }
    
    /**
     * Prepares the strategy for exploration.
     * @param gts - the current gts
     * @param state - the currently selected state (null if none is selected) 
     */
    public void prepare(GTS gts, GraphState state) {
        this.strategy.prepare(gts, state);
    }
        
    /**
     * Executes the exploration.
     * Expects that a LaunchThread (see Simulator.java) is currently active.
     * @return the set of results that have been stored within the acceptor during exploration
     */
    public Result play() {
        
        // initialize profiling and prepare graph listener
        reporter.start(RUNNING);
        this.strategy.addGTSListener(this.acceptor);
        this.interrupted = false;

        // start working until done or nothing to do
        while (!this.interrupted && !this.acceptor.getResult().done() && this.strategy.next()) {
            this.interrupted = Thread.currentThread().isInterrupted();
        }

        // remove graph listener and stop profiling       
        this.strategy.removeGTSListener(this.acceptor);
        reporter.stop();

        // return result
        return this.acceptor.getResult();
    }
    
    /**
     * Clears the Result set that is stored on the acceptor, which enables an earlier
     * performed exploration to be run again.
     */
    public void clearResult() {
        this.acceptor.setResult(this.acceptor.getResult().newInstance());
    }

    /**
     * Checks whether the LaunchThread has been interrupted during play().
     * @return the value of the internal boolean interrupted
     */
    public boolean isInterrupted() {
        return this.interrupted;
    }
    
    /**
     * Getter for the acceptor.
     * @return the acceptor of the exploration
     */
    public Acceptor getAcceptor() {
        return this.acceptor;
    }
    
    /**
     * Getter for the acceptor keyword.
     * @return the keyword of the acceptor of the exploration
     */
    public String getAcceptorKeyword() {
        return this.acceptorKeyword;
    }

    /** 
     * Returns the total running time of the exploration.
     * This information can be used for profiling.
     * @return the running time 
     */
    public long getRunningTime() {
        return reporter.getTotalTime(RUNNING);
    }
    
    /**
     * Returns the result of the exploration (which is stored on the acceptor). 
     * @return the result (set of graph states)
     */
    public Result getResult() {
        return this.acceptor.getResult();
    }
    
    /**
     * Returns a short String identification of the exploration, which is constructed
     * out of the stored keywords and the bound of the Result. 
     */
    public String getShortName() {
        String resultName;
        if (getResult().getBound() == 0)
            resultName = "*";
        else
            resultName = Integer.toString(getResult().getBound());        
        
        return (this.getStrategyKeyword() +
                "/" +
                this.getAcceptorKeyword() +
                "/" +
                resultName);
    }

    /**
     * Getter for the strategy keyword.
     * @return the keyword of the strategy of the exploration
     */
    public String getStrategyKeyword() {
        return this.strategyKeyword;
    }
 }