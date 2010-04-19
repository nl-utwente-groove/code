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
    static private final Reporter reporter =
        Reporter.register(DefaultScenario.class);
    static private final Reporter playReporter =
        reporter.register("playScenario()");

    /**
     * Initialise to a given exploration. 
     * @param strategy - strategy component of the exploration
     * @param acceptor - acceptor component of the exploration
     * @param nrResults - nrResults component of the exploration
     */
    public Exploration(Serialized strategy, Serialized acceptor, int nrResults) {
        this.strategy = strategy;
        this.acceptor = acceptor;
        this.nrResults = nrResults;
        this.lastResult = new Result(0);
    }

    /**
     * Initialize to the default exploration, which is formed by the default
     * strategy, the default acceptor and 0 (=infinite) results.  
     */
    public Exploration() {
        this(new Serialized("Breadth-First"), new Serialized("Final"), 0);
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
            buffer.append("Infinite");
        } else {
            buffer.append(this.nrResults);
        }
        return buffer.toString();
    }

    /**
     * Executes the exploration.
     * Expects that a LaunchThread (see Simulator.java) is currently active.
     * @param gts - the GTS on which the exploration will be performed
     * @param state - the state in which exploration will start (may be null)
     */
    public void play(GTS gts, GraphState state) throws FormatException {

        // parse the strategy
        Strategy parsedStrategy =
            new StrategyEnumerator().parse(gts, this.strategy);

        // parse the acceptor
        Acceptor parsedAcceptor =
            new AcceptorEnumerator().parse(gts, this.acceptor);

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
        reporter.stop();

        // store result
        this.lastResult = parsedAcceptor.getResult();
    }

    /**
     * Returns the total running time of the exploration.
     * This information can be used for profiling.
     * @return the long holding the running time in number of seconds 
     */
    public long getRunningTime() {
        return playReporter.getTotalTime();
    }
}