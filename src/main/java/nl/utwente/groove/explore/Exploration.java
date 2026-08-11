/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.explore;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import nl.utwente.groove.explore.engine.Strategy;
import nl.utwente.groove.explore.result.Acceptor;
import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.util.Reporter;
import nl.utwente.groove.util.parse.FormatException;

/**
 * A single exploration run: the application of an {@link ExploreType} to a
 * GTS from a given start state. Constructing the exploration instantiates
 * the type's strategy and acceptor for the grammar and prepares the GTS for
 * the run; {@link #play()} executes it. The result of the execution is
 * remembered in the exploration ({@link #getResult()}).
 * @author Maarten de Mol
 */
public class Exploration {
    /**
     * Creates an exploration with a given type and for a given GTS and (non-{@code null}) start state.
     */
    public Exploration(ExploreType type, GraphState start) throws FormatException {
        this.type = type;
        this.gts = start.getGTS();
        this.type.prepareRun(this.gts);
        Grammar grammar = this.gts.getGrammar();
        // realise the exploration type for the grammar
        var realisation = this.type.realise(grammar);
        this.strategy = realisation.strategy();
        this.acceptor = realisation.acceptor();
        // initialize acceptor and GTS
        this.strategy.setGTS(this.gts);
        this.strategy.setState(start);
        this.strategy.setAcceptor(this.acceptor);
    }

    private final Strategy strategy;
    private final Acceptor acceptor;

    /** Returns the type of this exploration. */
    public ExploreType getType() {
        return this.type;
    }

    private final ExploreType type;

    /**
     * Returns most recently explored GTS.
     */
    public GTS getGTS() {
        return this.gts;
    }

    private final GTS gts;

    /**
     * Returns the result of the most recent exploration.
     */
    public ExploreResult getResult() {
        return this.lastResult;
    }

    /** Result of the last exploration. */
    private ExploreResult lastResult;

    /**
     * Returns the state in which the most recent exploration ended.
     */
    public GraphState getLastState() {
        return this.lastState;
    }

    private GraphState lastState;

    /**
     * Returns the message of the last exploration.
     */
    public String getLastMessage() {
        return this.lastMessage;
    }

    /** Message of the last exploration. */
    private String lastMessage;

    /**
     * Indicates if the most recent exploration was manually interrupted.
     */
    public boolean isInterrupted() {
        return this.interrupted;
    }

    private boolean interrupted;

    /**
     * Executes the exploration.
     * Returns {@code this} for call chaining.
     */
    final public Exploration play() {
        // initialize profiling and prepare graph listener
        playReporter.start();
        for (ExplorationListener listener : this.listeners) {
            listener.start(this, this.gts);
        }
        this.strategy.play();
        this.interrupted = this.strategy.isInterrupted();
        for (ExplorationListener listener : this.listeners) {
            if (this.interrupted) {
                listener.abort(this.gts);
            } else {
                listener.stop(this.gts);
            }
        }
        // stop profiling
        playReporter.stop();

        // store result
        this.lastResult = this.acceptor.getResult();
        this.lastState = this.strategy.getLastState();
        this.lastMessage = this.strategy.getMessage();
        if (!this.gts.isStoring()) {
            // the discovered states were not stored; retain the traces of
            // the result states and of the last explored state, so that the
            // GTS afterwards shows what the exploration produced
            var tips = new LinkedHashSet<>(this.lastResult.getStates());
            if (this.lastState != null) {
                tips.add(this.lastState);
            }
            this.gts.retainTraces(tips);
            this.lastMessage += " (discovered %d states, retained %d)"
                .formatted(this.gts.getNextStateNr(), this.gts.nodeCount());
        }
        return this;
    }

    /**
     * Adds an exploration listener.
     * The listener will be notified of the start and end of all subsequent
     * explorations.
     */
    public void addListener(ExplorationListener listener) {
        this.listeners.add(listener);
    }

    /** Removes an exploration listener. */
    public void removeListener(ExplorationListener listener) {
        this.listeners.remove(listener);
    }

    /** List of currently active exploration listeners. */
    private List<ExplorationListener> listeners = new ArrayList<>();

    /** Returns the result of a default-type exploration (see {@link ExploreType#getDefault()}) of a given GTS.
     * @param gts the GTS on which the exploration is to be performed
     * @return the resulting exploration object
     * @throws FormatException if the grammar of {@code gts} is not
     * compatible with the default exploration type
     */
    static public final Exploration explore(GTS gts) throws FormatException {
        return ExploreType.getDefault().newExploration(gts, null)
            .play();
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
    static private final Reporter reporter = Reporter.register(Exploration.class);
    /** Handle for profiling {@link #play()}. */
    static final Reporter playReporter = reporter.register("playScenario()");
}