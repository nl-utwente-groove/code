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

import groove.explore.result.CycleAcceptor;
import groove.explore.result.Result;
import groove.explore.strategy.Boundary;
import groove.explore.strategy.BoundedModelCheckingStrategy;
import groove.explore.strategy.ModelCheckingStrategy;
import groove.lts.GTS;
import groove.lts.GraphState;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
public class ModelCheckingScenario extends DefaultScenario {
    /**
     * Creates a new named instance from a given strategy and acceptor.
     */
    public ModelCheckingScenario(ModelCheckingStrategy strategy, String name,
            String description) {
        super(strategy, new CycleAcceptor(strategy), name, description);
    }

    @Override
    public void prepare(GTS gts, GraphState state) {
        getStrategy().setProperty(getProperty());
        if (getStrategy() instanceof BoundedModelCheckingStrategy) {
            ((BoundedModelCheckingStrategy) getStrategy()).setBoundary(getBoundary());
        }
        super.prepare(gts, state);
    }

    @Override
    public Result play() {
        Result result = super.play();
        reportMemory();
        reportCounterExample();
        return result;
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
        return (ModelCheckingStrategy) super.getStrategy();
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
    protected String getProperty() {
        return this.property;
    }

    /** Callback method to get a boundary for the scenario. */
    protected Boundary getBoundary() {
        return this.boundary;
    }

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
