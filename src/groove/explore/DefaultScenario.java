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
 * $Id: DefaultScenario.java,v 1.6 2008/02/20 10:01:39 kastenberg Exp $
 */

package groove.explore;

import groove.explore.result.Acceptor;
import groove.explore.result.FinalStateAcceptor;
import groove.explore.result.Result;
import groove.explore.strategy.Strategy;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.util.Reporter;

// requirements:
//
// MAIN REQUIREMENTS:
// - adding states and transitions to the gts (yes or no, late or immediate)
// - iterating over the possible ruleapplications for a certain graphstate
// - should incorporate priorities and control
// - iterating over the possible graphstates
// - identifying when finished (a certain goal is reached)
// - some goals may not require or even rather not have the gts filled
// - requires stateiter to not use the gts
// - requires the scenario to be able to store intermediate states
// - ALTERNATITE: allow discarding "unsuccesful" results
//
// - ISSUE: WHO IS RESPONSIBLE FOR ADDINGS STATES/TRANSITIONS AND CLOSING STATES
//
// OTHER (sub) REQUIREMENTS:
// - ruleapplication iteration should incorporate priorities and control
// - the ruleappiter and stateiter together should iterate the entire statespace
// - if an iterator depends on stuff being added to the gts, then stuff should
// be added to the gts
// - do not use hasNext in iterators (or maybe dont call it iterator, because
// hasNext is sometimes
// hard to compute and decreases performance, unless caching of temp results is
// implemented
// CONSEQUENCE: if hasNext is not implemented, the only one able to close a
// state is the ruleapplication iterator itself
//
// alternatives - iterator usage:
// - the iterators might not be fully used, thus if caching is needed they
// should do their own or it should be handled by the getter method for the
// iterator (this is almost a solution already, but then again, using iterators
// is as well)
// - iterators are always completely used, so if only ONE ruleapplication (e.g.
// depth first) should be explored at a time, then it should only be able to
// execute next() once.
//
// alternatives - application-iterator caching:
// - one could store an iterator for a certain graphstate
// - one could let the iterator find the first ruleapplication that has not been
// added to the gts yet
//
// dangers, prevent:
// - continuing with an applicationiterator when a transition from a higher
// priority has already been found/added.

/**
 * A default implementation of a {@link groove.explore.Scenario}.
 * 
 * The two iterators combined should allow reaching the goals compatible with
 * this Scenario.
 * 
 * @author Staijen
 * 
 */
public class DefaultScenario implements Scenario {
    /** Creates a scenario with a given strategy and acceptor. */
    public DefaultScenario(Strategy strategy, Acceptor acceptor) {
        this(strategy, acceptor, null, null);
    }

    /** 
     * Creates a named scenario with a given strategy and acceptor.
     * @param acceptor the acceptor to be used; if <code>null</code>, a {@link FinalStateAcceptor}
     * will be created.
     * @param name the name of the scenario; may be <code>null</code> 
     * @param description one-line description of the scenario; may be <code>null</code> 
     */
    public DefaultScenario(Strategy strategy, Acceptor acceptor, String name,
            String description) {
        this.strategy = strategy;
        this.acceptor = acceptor == null ? new FinalStateAcceptor() : acceptor;
        this.name = name;
        this.description = description;
    }

    public void prepare(GTS gts) {
        prepare(gts, null);
    }

    public void prepare(GTS gts, GraphState state) {
        assert this.acceptor != null && this.strategy != null : "The scenario is not correctly initialized with a result, a strategy and an acceptor.";
        assert (gts != null) : "The GTS of the scenario has not been initialized.";
        this.gts = gts;
        this.acceptor = this.acceptor.newInstance();
        // make sure strategy and acceptor are reset and up to date
        this.strategy.prepare(gts, state);
    }

    public Result play() {
        reporter.start(RUNNING);

        this.strategy.addGTSListener(this.acceptor);
        this.interrupted = false;

        // start working until done or nothing to do
        while (!this.interrupted && !getResult().done() && this.strategy.next()) {
            this.interrupted = Thread.currentThread().isInterrupted();
        }

        this.strategy.removeGTSListener(this.acceptor);
        reporter.stop();

        // return result
        return getResult();
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

    public Strategy getStrategy() {
        return this.strategy;
    }

    /** Returns the acceptor for this scenario. */
    protected Acceptor getAcceptor() {
        return this.acceptor;
    }

    public String getDescription() {
        return this.description;
    }

    public String getName() {
        return this.name;
    }

    /** Returns the scenario description. */
    @Override
    public String toString() {
        return getDescription();
    }

    /** Returns the GTS for which this scenario was last prepared. */
    protected GTS getGTS() {
        return this.gts;
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
    private final Strategy strategy;
    /** Name of this scenario. */
    private final String name;
    /** One-line description of this scenario. */
    private final String description;

    /** Reporter for profiling information; aliased to {@link GTS#reporter}. */
    static private final Reporter reporter =
        Reporter.register(DefaultScenario.class);
    /** Handle for profiling {@link #prepare(GTS)}. */
    static private final int RUNNING = reporter.newMethod("playScenario()");

    /** Returns the total running time of {@link #prepare(GTS)}. */
    public static long getRunningTime() {
        return reporter.getTotalTime(RUNNING);
    }
}
