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
 * $Id: Exploration.java 5703 2015-04-03 08:27:26Z rensink $
 */
package nl.utwente.groove.explore;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.explore.config.ExploreConfig;
import nl.utwente.groove.explore.config.ExploreTypeConverter;
import nl.utwente.groove.explore.engine.Strategy;
import nl.utwente.groove.explore.result.Acceptor;
import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.Factory;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;

/**
 * An ExploreType determines what an exploration does: it instantiates the
 * strategy and acceptor for a given grammar, and determines the number of
 * results after which exploration halts. Most explorations are configuration-based (see
 * {@code ConfiguredExploreType}); the model-checking explorations, which
 * the exploration feature model deliberately does not cover, have a
 * dedicated subclass. To use an {@link ExploreType}, it
 * should be fed into an {@link Exploration}.
 * @author Arend Rensink
 */
@NonNullByDefault
public abstract class ExploreType {
    /**
     * Initialises the exploration type.
     * @param count number of results after which exploration halts:
     * {@code 0} means exploration continues until the strategy is exhausted
     */
    protected ExploreType(int count) {
        this.count = count;
    }

    private final int count;

    /**
     * Returns the result count: the number of results after which exploration
     * halts, with {@code 0} meaning exploration continues until the strategy
     * is exhausted.
     * @see #withResultCount(int)
     */
    public int getResultCount() {
        return this.count;
    }

    /**
     * Returns a string that identifies the exploration, for display purposes.
     * @return the identifying string
     */
    abstract public String getIdentifier();

    /**
     * The run-time realisation of an exploration type for a given grammar:
     * a strategy and acceptor pair, mutually wired where the exploration
     * type requires it.
     */
    public record Realisation(Strategy strategy, Acceptor acceptor) {
        // no additional functionality
    }

    /**
     * Realises this exploration type for a given graph grammar, by
     * instantiating its strategy and acceptor, with the result count of
     * this exploration type applied. Both objects are stateful and good for
     * a single exploration run; every call returns fresh instances.
     * @throws FormatException if the grammar is incompatible with this
     * exploration type
     */
    abstract public Realisation realise(Grammar grammar) throws FormatException;

    /**
     * Returns a variant of this exploration type with a different result
     * count ({@code 0} meaning unbounded).
     * @throws IllegalArgumentException if the count is inconsistent with
     * this exploration type
     */
    abstract public ExploreType withResultCount(int count);

    /**
     * Indicates if an exploration of this type presents its result as traces
     * from the start state to the result states, rather than as the result
     * states themselves. Consumers may use this to switch to a trace view of
     * the explored LTS. This implementation returns {@code false}.
     */
    @AIGenerated("Claude Fable 5, 2026-08")
    public boolean presentsResultAsTraces() {
        return false;
    }

    /**
     * Tests if this exploration is compatible with a given rule system.
     * If this method does not throw an exception, then neither will {@link #newExploration}.
     * @throws FormatException if the rule system is not compatible
     */
    public void test(Grammar grammar) throws FormatException {
        setTestGrammar(grammar);
        this.grammarErrors.get().throwException();
    }

    private void setTestGrammar(Grammar grammar) {
        if (this.testGrammar != grammar) {
            this.testGrammar = grammar;
            this.grammarErrors.reset();
        }
    }

    private Grammar getTestGrammar() {
        var result = this.testGrammar;
        assert result != null;
        return result;
    }

    private @Nullable Grammar testGrammar;

    private Factory<FormatErrorSet> grammarErrors = Factory.lazy(() -> {
        FormatErrorSet errors = new FormatErrorSet();
        try {
            realise(getTestGrammar());
        } catch (FormatException exc) {
            errors.addAll(exc.getErrors());
        }
        return errors;
    });

    /**
     * Callback method allowing the exploration type to apply its per-GTS
     * features (collapse mode, algebra family, persistence) to a given GTS.
     * May only be called on a fresh GTS: the features are baked into the
     * rule application record and the start state, so they cannot change
     * once the start state has materialised (asserted here). Called from
     * {@link #newExploration} and from the Simulator's GTS reset.
     * This implementation does nothing.
     */
    public void prepareGTS(GTS gts) {
        if (!gts.isFresh()) {
            throw Exceptions.illegalState("Per-GTS features can only be applied to a fresh GTS");
        }
        // no per-GTS features by default
    }

    /**
     * Callback method preparing a GTS for a single exploration run of this
     * type; called once per run, from the {@link Exploration} constructor.
     * Verifies that the GTS is compatible with the per-GTS features of this
     * type and (re-)engages the operational switches for the run. This
     * implementation refuses a GTS that does not persist its states: an
     * exploration type without a persistence feature of its own would
     * store every discovered state into a state set that no longer
     * collapses (see {@link GTS#retainTraces}).
     * @throws FormatException if the GTS was explored under per-GTS
     * features that are inconsistent with this exploration type
     */
    public void prepareRun(GTS gts) throws FormatException {
        if (!gts.isPersistent()) {
            throw new FormatException(
                "This exploration cannot continue a state space explored without persistence;"
                    + " use Restart");
        }
    }

    /**
     * Factory method for an exploration based on this type.
     * @param gts the GTS on which the exploration will be performed
     * @throws FormatException if the rule system of {@code gts} is not
     * compatible with this exploration
     * @see #test(Grammar)
     */
    final public Exploration newExploration(GTS gts) throws FormatException {
        return newExploration(gts, null);
    }

    /**
     * Factory method for an exploration based on this type.
     * @param gts the GTS on which the exploration will be performed
     * @param start the state in which exploration will start; if {@code null},
     * the GTS start state is used
     * @throws FormatException if the rule system of {@code gts} is not
     * compatible with this exploration
     * @see #test(Grammar)
     */
    final public Exploration newExploration(GTS gts,
                                            @Nullable GraphState start) throws FormatException {
        if (start == null) {
            if (gts.isFresh()) {
                // apply the per-GTS features before materialising the start
                // state below
                prepareGTS(gts);
            }
            start = gts.startState();
        }
        return new Exploration(this, start);
    }

    @Override
    public String toString() {
        return getIdentifier();
    }

    /** Returns the default exploration (breadth-first, final states,
     * unbounded): the realisation of the default exploration configuration. */
    static public ExploreType getDefault() {
        return DEFAULT.get();
    }

    /** The lazily computed realisation of the default configuration; lazy so
     * that a realisation failure surfaces at the point of use rather than as
     * an {@link ExceptionInInitializerError} of this class. */
    static private final Factory<ExploreType> DEFAULT = Factory.lazy(() -> {
        try {
            return ExploreTypeConverter.toExploreType(new ExploreConfig());
        } catch (FormatException exc) {
            throw Exceptions.illegalState("Default exploration configuration is unrealisable: %s",
                                          exc.getMessage());
        }
    });
}
