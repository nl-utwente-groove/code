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

import groove.explore.encode.Serialized;
import groove.explore.result.Acceptor;
import groove.explore.result.Result;
import groove.explore.strategy.Strategy;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.trans.GraphGrammar;
import groove.util.Reporter;
import groove.view.FormatException;

/**
 * <!=========================================================================>
 * An Exploration is a combination of a serialized strategy, a serialized
 * acceptor and a number of results. By parsing its fields (relative to the
 * Simulator), the exploration can be executed. The result of the execution
 * (which is a Result set) is remembered in the Exploration.
 * <!=========================================================================>
 * @author Maarten de Mol
 */
public class Exploration {

    private final Serialized strategy;
    private final Serialized acceptor;
    private final int nrResults;

    private Result lastResult;

    private boolean interrupted;

    /**
     * Initialise to a given exploration. 
     * @param strategy strategy component of the exploration
     * @param acceptor acceptor component of the exploration
     * @param nrResults number of results: {@code 0} means unbounded
     */
    public Exploration(Serialized strategy, Serialized acceptor, int nrResults) {
        this.strategy = strategy;
        this.acceptor = acceptor;
        this.nrResults = nrResults;
        this.lastResult = new Result(0);
    }

    /**
     * Initialise to a given exploration, by named strategy and acceptor
     * @param strategy name of the strategy component
     * @param acceptor name of the acceptor component
     * @param nrResults number of results: {@code 0} means unbounded
     */
    public Exploration(String strategy, String acceptor, int nrResults) {
        this(new Serialized(strategy), new Serialized(acceptor), nrResults);
    }

    /**
     * Initialise to a given exploration, by named strategy and acceptor
     * @param strategy strategy component value
     * @param acceptor acceptor component value
     * @param nrResults number of results: {@code 0} means unbounded
     */
    public Exploration(StrategyValue strategy, AcceptorValue acceptor,
            int nrResults) {
        this(new Serialized(strategy.getKeyword()), new Serialized(
            acceptor.getKeyword()), nrResults);
    }

    /**
     * Initialises to the default exploration, which is formed by the BFS
     * strategy, the final acceptor and 0 (=infinite) results.  
     */
    public Exploration() {
        this("bfs", "final", 0);
    }

    /**
     * Getter for the strategy.
     */
    public Serialized getStrategy() {
        return this.strategy;
    }

    /**
     * Getter for the acceptor.
     */
    public Serialized getAcceptor() {
        return this.acceptor;
    }

    /**
     * Getter for the number of results.
     */
    public int getNrResults() {
        return this.nrResults;
    }

    /**
     * Getter for the result of the last exploration. 
     */
    public Result getLastResult() {
        return this.lastResult;
    }

    /**
     * Getter for the isInterrupted flag. 
     */
    public Boolean isInterrupted() {
        return this.interrupted;
    }

    /**
     * Returns a string that identifies the exploration.
     * @return the identifying string
     */
    public String getIdentifier() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("");
        buffer.append(this.strategy.toString());
        buffer.append(" / ");
        buffer.append(this.acceptor.toString());
        buffer.append(" / ");
        if (this.nrResults == 0) {
            buffer.append("infinite");
        } else {
            buffer.append(this.nrResults);
        }
        return buffer.toString();
    }

    /** 
     * Tests if this exploration is compatible with a given rule system.
     * If this method does not throw an exception, then neither will {@link #play(GTS, GraphState)}.
     * @throws FormatException if the rule system is not compatible
     */
    public void test(GraphGrammar rules) throws FormatException {
        StrategyEnumerator.parseStrategy(rules, this.strategy);
        AcceptorEnumerator.parseAcceptor(rules, this.acceptor);
    }

    /**
     * Executes the exploration.
     * Expects that a LaunchThread (see Simulator.java) is currently active.
     * @param gts - the GTS on which the exploration will be performed
     * @param state - the state in which exploration will start (may be null)
     * @throws FormatException if the rule system of {@code gts} is not
     * compatible with this exploration
     * @see #test(GraphGrammar)
     */
    final public void play(GTS gts, GraphState state) throws FormatException {
        GraphGrammar rules = gts.getGrammar().getRuleSystem();
        // parse the strategy
        Strategy parsedStrategy =
            StrategyEnumerator.parseStrategy(rules, this.strategy);

        // parse the acceptor
        Acceptor parsedAcceptor =
            AcceptorEnumerator.parseAcceptor(rules, this.acceptor);

        // initialize acceptor and GTS
        parsedAcceptor.setResult(new Result(this.nrResults));
        parsedStrategy.prepare(gts, state);

        // initialize profiling and prepare graph listener
        playReporter.start();
        parsedStrategy.addGTSListener(parsedAcceptor);
        this.interrupted = false;

        // start working until done or nothing to do
        while (!this.interrupted && !parsedAcceptor.getResult().done()
            && parsedStrategy.next()) {
            this.interrupted = Thread.currentThread().isInterrupted();
        }

        // remove graph listener and stop profiling       
        parsedStrategy.removeGTSListener(parsedAcceptor);
        playReporter.stop();

        // store result
        this.lastResult = parsedAcceptor.getResult();
    }

    /**
     * Returns the total running time of the exploration.
     * This information can be used for profiling.
     * @return the long holding the running time in number of seconds 
     */
    static public long getRunningTime() {
        return playReporter.getTotalTime();
    }

    /** Reporter for profiling information. */
    static private final Reporter reporter =
        Reporter.register(Exploration.class);
    /** Handle for profiling {@link #play(GTS, GraphState)}. */
    static final Reporter playReporter = reporter.register("playScenario()");
}