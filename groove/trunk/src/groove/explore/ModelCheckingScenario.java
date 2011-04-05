/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
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
package groove.explore;

import groove.explore.result.Acceptor;
import groove.explore.result.CycleAcceptor;
import groove.explore.result.Result;
import groove.explore.strategy.Boundary;
import groove.explore.strategy.BoundedModelCheckingStrategy;
import groove.explore.strategy.ModelCheckingStrategy;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.util.Reporter;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
public class ModelCheckingScenario implements Scenario {
    /**
     * Creates a new named instance from a given strategy and acceptor.
     */
    public ModelCheckingScenario(ModelCheckingStrategy strategy, String name) {
        this.strategy = strategy;
        this.acceptor = new CycleAcceptor(strategy);
        this.name = name;
    }

    public void prepare(GTS gts) {
        prepare(gts, null);
    }

    /**
     * Returns the result of this scenario. The result is retrieved from the
     * acceptor; it is an error to call this method if no acceptor is set.
     */
    public Result getResult() {
        return this.acceptor.getResult();
    }

    public boolean isInterrupted() {
        return this.interrupted;
    }

    /** Returns the acceptor for this scenario. */
    protected Acceptor getAcceptor() {
        return this.acceptor;
    }

    public String getName() {
        return this.name;
    }

    /** Returns the scenario description. */
    @Override
    public String toString() {
        return getName();
    }

    /** Returns the GTS for which this scenario was last prepared. */
    protected GTS getGTS() {
        return this.gts;
    }

    @Override
    public void prepare(GTS gts, GraphState state) {
        getStrategy().setProperty(getProperty());
        if (getStrategy() instanceof BoundedModelCheckingStrategy) {
            ((BoundedModelCheckingStrategy) getStrategy()).setBoundary(getBoundary());
        }
        // model checking always starts at the initial state
        assert this.acceptor != null && this.strategy != null : "The scenario is not correctly initialized with a result, a strategy and an acceptor.";
        assert (gts != null) : "The GTS of the scenario has not been initialized.";
        this.gts = gts;
        this.acceptor = this.acceptor.newInstance();
        // make sure strategy and acceptor are reset and up to date
        this.strategy.prepare(gts, gts.startState());
    }

    @Override
    public Result play() {
        playReporter.start();

        this.strategy.addGTSListener(this.acceptor);
        this.interrupted = false;

        // start working until done or nothing to do
        while (!this.interrupted && !getResult().done() && this.strategy.next()) {
            this.interrupted = Thread.currentThread().isInterrupted();
        }

        this.strategy.removeGTSListener(this.acceptor);
        playReporter.stop();

        // return result
        reportMemory();
        reportCounterExample();
        return getResult();
    }

    /**
     * Prints a report on {@link System#err} on memory usage, if
     * {@link #REPORT_MEMORY} is set.
     */
    private void reportMemory() {
        if (REPORT_MEMORY) {
            Runtime runtime = Runtime.getRuntime();
            System.runFinalization();
            System.gc();
            long usedMemory = runtime.totalMemory() - runtime.freeMemory();
            System.err.println("Memory in use: " + (usedMemory / 1024) + " kB");
        }
    }

    /**
     * Prints a report on {@link System#err} on memory usage, if
     * {@link #REPORT_COUNTER_EXAMPLE} is set.
     */
    @SuppressWarnings("all")
    private void reportCounterExample() {
        if (REPORT_COUNTER_EXAMPLE && !getResult().getValue().isEmpty()) {
            System.err.println("A counter-example of length "
                + getResult().getValue().size() + " has been found: "
                + getResult().getValue());
        }
    }

    @Override
    public ModelCheckingStrategy getStrategy() {
        return this.strategy;
    }

    /**
     * Sets the property to be model checked.
     * It is required that this can be parsed correctly.
     */
    public void setProperty(String property) {
        this.property = property;
    }

    /**
     * Sets the boundary for model checking (in case the strategy is a
     * {@link BoundedModelCheckingStrategy}).
     */
    public void setBoundary(Boundary boundary) {
        this.boundary = boundary;
    }

    /** Callback method to get a property for the scenario. */
    public String getProperty() {
        return this.property;
    }

    /** Callback method to get a boundary for the scenario. */
    protected Boundary getBoundary() {
        return this.boundary;
    }

    /** The GTS for which this scenario was last prepared. */
    private GTS gts;
    /**
     * Flag indicating that the last invocation of {@link #prepare(GTS)} was
     * interrupted.
     */
    private boolean interrupted;
    /**
     * The acceptor of the scenario.
     */
    private Acceptor acceptor;
    /**
     * The strategy used by this scenario.
     */
    private final ModelCheckingStrategy strategy;
    /** Name of this scenario. */
    private final String name;
    /** Reporter for profiling information. */
    static private final Reporter playReporter = Exploration.playReporter;
    /**
     * The boundary for model checking, in case the strategy is a
     * {@link BoundedModelCheckingStrategy}.
     */
    private Boundary boundary;
    /** The property to be model checked. */
    private String property;

    /**
     * Global flag to determine if memory usage is reported on
     * {@link System#err}.
     */
    static private final boolean REPORT_MEMORY = false;
    /**
     * Global flag to determine if counter-examples are reported on
     * {@link System#err}.
     */
    static private final boolean REPORT_COUNTER_EXAMPLE = false;
}
