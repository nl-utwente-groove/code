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
import groove.explore.result.CycleAcceptor;
import groove.explore.result.Result;
import groove.explore.strategy.LTLStrategy;
import groove.explore.strategy.Strategy;
import groove.explore.strategy.Strategy.Halter;
import groove.grammar.Grammar;
import groove.grammar.model.FormatErrorSet;
import groove.grammar.model.FormatException;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.util.Reporter;

import java.util.ArrayList;
import java.util.List;

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
    private GTS lastGts;
    /** Result of the last exploration. */
    private Result lastResult;
    /** Message of the last exploration. */
    private String lastMessage;

    private GraphState lastState;

    /** List of currently active exploration listeners. */
    private List<ExplorationListener> listeners =
        new ArrayList<ExplorationListener>();

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
        this(strategy.toSerialized(), acceptor.toSerialized(), nrResults);
    }

    /**
     * Initialises to the default exploration, which is formed by the BFS
     * strategy, the final acceptor and 0 (=infinite) results.  
     */
    public Exploration() {
        this("bfs", "final", 0);
    }

    /**
     * Getter for the serialised strategy.
     */
    public Serialized getStrategy() {
        return this.strategy;
    }

    /**
     * Returns the strategy, instantiated for a given graph grammar.
     * @throws FormatException if the grammar is incompatible with the (serialised)
     * strategy.
     */
    public Strategy getParsedStrategy(Grammar grammar) throws FormatException {
        return StrategyEnumerator.parseStrategy(grammar, this.strategy);
    }

    /**
     * Getter for the serialised acceptor.
     */
    public Serialized getAcceptor() {
        return this.acceptor;
    }

    /**
     * Returns the acceptor, instantiated for a given graph grammar.
     * @throws FormatException if the grammar is incompatible with the (serialised)
     * acceptor.
     */
    public Acceptor getParsedAcceptor(Grammar grammar) throws FormatException {
        if (getParsedStrategy(grammar) instanceof LTLStrategy) {
            return new CycleAcceptor();
        } else {
            return AcceptorEnumerator.parseAcceptor(grammar, this.acceptor);
        }
    }

    /**
     * Returns most recently explored GTS.
     */
    public GTS getGTS() {
        return this.lastGts;
    }

    /**
     * Returns the number of results of the most recent exploration.
     */
    public int getNrResults() {
        return this.nrResults;
    }

    /**
     * Returns the result of the most recent exploration. 
     */
    public Result getResult() {
        return this.lastResult;
    }

    /**
     * Returns the state in which the most recent exploration ended. 
     */
    public GraphState getLastState() {
        return this.lastState;
    }

    /**
     * Returns the message of the last exploration. 
     */
    public String getLastMessage() {
        return this.lastMessage;
    }

    /**
     * Indicates if the most recent exploration was manually interrupted. 
     */
    public boolean isInterrupted() {
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
    public void test(Grammar grammar) throws FormatException {
        FormatErrorSet errors = new FormatErrorSet();
        try {
            getParsedStrategy(grammar);
        } catch (FormatException exc) {
            errors.addAll(exc.getErrors());
        }
        try {
            getParsedAcceptor(grammar);
        } catch (FormatException exc) {
            errors.addAll(exc.getErrors());
        }
        errors.throwException();
    }

    /**
     * Executes the exploration.
     * @param gts - the GTS on which the exploration will be performed
     * @param state - the state in which exploration will start (may be null)
     * @throws FormatException if the rule system of {@code gts} is not
     * compatible with this exploration
     * @see #test(Grammar)
     */
    final public void play(GTS gts, GraphState state) throws FormatException {
        this.lastGts = gts;
        Grammar grammar = gts.getGrammar();
        // parse the strategy
        Strategy parsedStrategy = getParsedStrategy(grammar);

        // parse the acceptor
        final Acceptor parsedAcceptor = getParsedAcceptor(grammar);

        // initialize acceptor and GTS
        parsedAcceptor.setResult(new Result(this.nrResults));
        parsedStrategy.setGTS(gts);
        parsedStrategy.setState(state);

        // initialize profiling and prepare graph listener
        playReporter.start();
        parsedStrategy.setAcceptor(parsedAcceptor);
        for (ExplorationListener listener : this.listeners) {
            listener.start(this, gts);
        }
        parsedStrategy.play(new Halter() {
            @Override
            public boolean halt() {
                return parsedAcceptor.getResult().done();
            }
        });
        this.interrupted = parsedStrategy.isInterrupted();
        for (ExplorationListener listener : this.listeners) {
            if (this.interrupted) {
                listener.abort(gts);
            } else {
                listener.stop(gts);
            }
        }
        // stop profiling    
        playReporter.stop();

        // store result
        this.lastResult = parsedAcceptor.getResult();
        this.lastState = parsedStrategy.getLastState();
        this.lastMessage = parsedStrategy.getMessage();
    }

    /** 
     * Returns a string that, when used as input for {@link #parse(String)},
     * will return an exploration equal to this one.
     */
    public String toParsableString() {
        String result =
            StrategyEnumerator.toParsableStrategy(this.strategy) + " "
                + AcceptorEnumerator.toParsableAcceptor(this.acceptor) + " "
                + this.nrResults;
        return result;
    }

    @Override
    public String toString() {
        return toParsableString();
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

    /** 
     * Parses an exploration description into an exploration instance.
     * The description must be a list of two or three space-separated substrings:
     * <li> The first value is the name of the strategy
     * <li> The second value is the name of the acceptor
     * <li> the (optional) third value is the number of expected results; 
     * if omitted, the number is infinite
     * @param description the exploration description to be parsed
     * @return the parsed exploration (non-{@code null})
     * @throws FormatException if the description could not be parsed
     */
    static public Exploration parse(String description) throws FormatException {
        String[] parts = description.split("\\s");
        if (parts.length < 2 || parts.length > 3) {
            throw new FormatException(SYNTAX_MESSAGE);
        }
        Serialized strategy = strategies.parseCommandline(parts[0]);
        Serialized acceptor = acceptors.parseCommandline(parts[1]);
        int resultCount = 0;
        if (parts.length == 3) {
            String countMessage =
                String.format(
                    "Result count '%s' must be a non-negative number", parts[2]);
            try {
                resultCount = Integer.parseInt(parts[2]);
            } catch (NumberFormatException e) {
                throw new FormatException(countMessage);
            }
            if (resultCount < 0) {
                throw new FormatException(countMessage);
            }
        }
        return new Exploration(strategy, acceptor, resultCount);
    }

    /**
     * Returns the total running time of the exploration.
     * This information can be used for profiling.
     * @return the long holding the running time in number of seconds 
     */
    static public long getRunningTime() {
        return playReporter.getTotalTime();
    }

    /** Message describing the syntax of a parsable exploration strategy. */
    static public final String SYNTAX_MESSAGE =
        "Exploration syntax: \"<strategy> <acceptor> [<resultcount>]\"";
    /** Static instance of the strategy enumerator. */
    static private final StrategyEnumerator strategies =
        StrategyEnumerator.instance();
    /** Static instance of the acceptor enumerator. */
    static private final AcceptorEnumerator acceptors =
        AcceptorEnumerator.instance();
    /** Reporter for profiling information. */
    static private final Reporter reporter =
        Reporter.register(Exploration.class);
    /** Handle for profiling {@link #play(GTS, GraphState)}. */
    static final Reporter playReporter = reporter.register("playScenario()");
}