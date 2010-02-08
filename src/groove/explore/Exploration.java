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
import groove.gui.Simulator;
import groove.gui.dialog.ErrorDialog;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.util.Reporter;

import javax.swing.JDialog;

/**
 * Wrapper class for explorations. Provides functionality for storing and
 * executing an exploration. Also remembers the Result set of the last time
 * the exploration was executed. 
 * An exploration is given by a combination of a serialized strategy, a
 * serialized acceptor, and the number of results.
 *
 * @author Maarten de Mol
 * @version $Revision $
 */
public class Exploration {
    // The serialized strategy.
    private Serialized<Strategy> strategy;
    // The serialized acceptor.
    private Serialized<Acceptor> acceptor;
    // The number of results to store.
    private int nrResults;
    // The result set of the last exploration.
    private Result lastResult;

    // Internal info, needed during execution of the exploration.
    private boolean interrupted;
    static private final Reporter reporter =
        Reporter.register(DefaultScenario.class);
    static private final int RUNNING = reporter.newMethod("playScenario()");

    /**
     * Initialize an exploration. 
     * @param strategy - strategy component of the exploration
     * @param acceptor - acceptor component of the exploration
     * @param nrResults - nrResults component of the exploration
     */
    public Exploration(Serialized<Strategy> strategy,
            Serialized<Acceptor> acceptor, int nrResults) {
        this.strategy = strategy;
        this.acceptor = acceptor;
        this.nrResults = nrResults;
        this.lastResult = new Result(0);
    }

    /**
     * Initializes a default exploration (breadth-first, final states,
     * infinite results).
     */
    public Exploration() {
        this(new Serialized<Strategy>("Breadth-First", new BFSStrategy()),
            new Serialized<Acceptor>("Final", new FinalStateAcceptor()), 0);
    }

    /**
     * Returns a string that identifies the exploration.
     * @return the identifying string
     */
    public String getIdentifier() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("(");
        buffer.append(this.strategy.getIdentifier());
        buffer.append(", ");
        buffer.append(this.acceptor.getIdentifier());
        buffer.append(", ");
        if (this.nrResults == 0) {
            buffer.append("Infinite");
        } else {
            buffer.append(this.nrResults);
        }
        buffer.append(")");
        return buffer.toString();
    }

    /**
     * Getter for the serialized strategy.
     * @return the serialized strategy
     */
    public Serialized<Strategy> getStrategy() {
        return this.strategy;
    }

    /**
     * Getter for the serialized acceptor.
     * @return the serialized acceptor
     */
    public Serialized<Acceptor> getAcceptor() {
        return this.acceptor;
    }

    /**
     * Getter for the number of results.
     * @return the number of results
     */
    public int getNrResults() {
        return this.nrResults;
    }

    /**
     * Returns the result of the last exploration. 
     * @return the result (set of graph states)
     */
    public Result getLastResult() {
        return this.lastResult;
    }

    /**
     * Executes the exploration.
     * Expects that a LaunchThread (see Simulator.java) is currently active.
     * @param simulator - reference to the Simulator, needed to materialize
     * @param gts - reference to the GTS, which must be prepared
     * @param state - the state in which exploration will start (may be null)
     */
    public void play(Simulator simulator, GTS gts, GraphState state) {

        // materialize
        Strategy strategy = this.strategy.materialize(simulator);
        Acceptor acceptor = this.acceptor.materialize(simulator);

        if (strategy == null || acceptor == null) {
            StringBuffer errorMessage = new StringBuffer();
            errorMessage.append("<HTML>Unable to bind the exploration");
            errorMessage.append("<BR><FONT color=#FF4500><B>");
            errorMessage.append(getIdentifier());
            errorMessage.append("</B></FONT><BR>");
            errorMessage.append("to the current grammar.<BR><P>");

            JDialog errorDialog =
                new ErrorDialog(simulator.getFrame(), errorMessage.toString(),
                    null);
            errorDialog.setVisible(true);
            return;
        }

        // initialize acceptor and GTS
        acceptor.setResult(new Result(this.nrResults));
        strategy.prepare(gts, state);

        // initialize profiling and prepare graph listener
        reporter.start(RUNNING);
        strategy.addGTSListener(acceptor);
        this.interrupted = false;

        // start working until done or nothing to do
        while (!this.interrupted && !acceptor.getResult().done()
            && strategy.next()) {
            this.interrupted = Thread.currentThread().isInterrupted();
        }

        // remove graph listener and stop profiling       
        strategy.removeGTSListener(acceptor);
        reporter.stop();

        // store result
        this.lastResult = acceptor.getResult();
    }

    /**
     * Checks whether the LaunchThread has been interrupted during play().
     * @return the value of the internal boolean interrupted
     */
    public boolean isInterrupted() {
        return this.interrupted;
    }

    /** 
     * Returns the total running time of the exploration.
     * This information can be used for profiling.
     * @return the running time 
     */
    public long getRunningTime() {
        return reporter.getTotalTime(RUNNING);
    }
}