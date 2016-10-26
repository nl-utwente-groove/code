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
 * $Id: Exploration.java 5703 2015-04-03 08:27:26Z rensink $
 */
package groove.explore;

import groove.explore.encode.Serialized;
import groove.explore.result.Acceptor;
import groove.explore.result.CycleAcceptor;
import groove.explore.strategy.LTLStrategy;
import groove.explore.strategy.Strategy;
import groove.grammar.Grammar;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.util.parse.FormatErrorSet;
import groove.util.parse.FormatException;
import groove.util.parse.Parser;

/**
 * An ExploreType is a combination of a serialized strategy, a serialized
 * acceptor and a bound to the number of results.
 * To use the {@link ExploreType}, it should be fed into an {@link Exploration}.
 * @author Arend Rensink
 */
public class ExploreType {
    private final Serialized strategy;
    private final Serialized acceptor;
    private final int bound;

    /**
     * Initialise to a given exploration.
     * @param strategy strategy component of the exploration; non-{@code null}
     * @param acceptor acceptor component of the exploration; non-{@code null}
     * @param bound number of results: {@code 0} means unbounded
     */
    public ExploreType(Serialized strategy, Serialized acceptor, int bound) {
        assert strategy != null;
        this.strategy = strategy;
        assert acceptor != null;
        this.acceptor = acceptor;
        this.bound = bound;
    }

    /**
     * Initialise to a given exploration, by named strategy and acceptor
     * @param strategy name of the strategy component
     * @param acceptor name of the acceptor component
     * @param nrResults number of results: {@code 0} means unbounded
     */
    public ExploreType(String strategy, String acceptor, int nrResults) {
        this(new Serialized(strategy), new Serialized(acceptor), nrResults);
    }

    /**
     * Initialise to a given exploration, by named strategy and acceptor
     * @param strategy strategy component value
     * @param acceptor acceptor component value
     * @param nrResults number of results: {@code 0} means unbounded
     */
    public ExploreType(StrategyValue strategy, AcceptorValue acceptor, int nrResults) {
        this(strategy.toSerialized(), acceptor.toSerialized(), nrResults);
    }

    /**
     * Initialises to the default exploration, which is formed by the BFS
     * strategy, the final acceptor and 0 (=infinite) results.
     */
    private ExploreType() {
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
     * Returns a prototype acceptor, instantiated for a given graph grammar.
     * @throws FormatException if the grammar is incompatible with the (serialised)
     * acceptor.
     */
    public Acceptor getParsedAcceptor(Grammar grammar) throws FormatException {
        if (getParsedStrategy(grammar) instanceof LTLStrategy) {
            return CycleAcceptor.PROTOTYPE;
        } else {
            return AcceptorEnumerator.parseAcceptor(grammar, this.acceptor);
        }
    }

    /**
     * Returns the exploration bound.
     */
    public int getBound() {
        return this.bound;
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
        if (this.bound == 0) {
            buffer.append("infinite");
        } else {
            buffer.append(this.bound);
        }
        return buffer.toString();
    }

    /**
     * Tests if this exploration is compatible with a given rule system.
     * If this method does not throw an exception, then neither will {@link #newExploration}.
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
     * Factory method for an exploration based on this type.
     * @param gts the GTS on which the exploration will be performed
     * @throws FormatException if the rule system of {@code gts} is not
     * compatible with this exploration
     * @see #test(Grammar)
     */
    final public Exploration newExploration(GTS gts) throws FormatException {
        return new Exploration(this, gts.startState());
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
    final public Exploration newExploration(GTS gts, GraphState start) throws FormatException {
        return new Exploration(this, start == null ? gts.startState() : start);
    }

    /**
     * Returns a string that, when used as input for {@link #parse(String)},
     * will return an exploration equal to this one.
     */
    public String toParsableString() {
        String result = StrategyEnumerator.toParsableStrategy(this.strategy) + " "
            + AcceptorEnumerator.toParsableAcceptor(this.acceptor) + " " + this.bound;
        return result;
    }

    @Override
    public String toString() {
        return toParsableString();
    }

    /** Parser for serialised explorations. */
    static public Parser<ExploreType> parser() {
        return new Parser<ExploreType>() {
            @Override
            public String getDescription() {
                return SYNTAX_MESSAGE;
            }

            @Override
            public boolean accepts(String text) {
                if (text == null || text.length() == 0) {
                    return true;
                }
                try {
                    ExploreType.parse(text);
                    return true;
                } catch (FormatException exc) {
                    return false;
                }
            }

            @Override
            public ExploreType parse(String input) {
                if (input == null || input.length() == 0) {
                    return null;
                }
                try {
                    return ExploreType.parse(input);
                } catch (FormatException exc) {
                    return null;
                }
            }

            @Override
            public String toParsableString(Object value) {
                if (value == null) {
                    return "";
                } else {
                    return ((ExploreType) value).toParsableString();
                }
            }

            @Override
            public Class<ExploreType> getValueType() {
                return ExploreType.class;
            }

            @Override
            public boolean isValue(Object value) {
                return value == null || value instanceof ExploreType;
            }

            @Override
            public ExploreType getDefaultValue() {
                return null;
            }
        };
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
    static public ExploreType parse(String description) throws FormatException {
        String[] parts = description.split("\\s");
        if (parts.length < 2 || parts.length > 3) {
            throw new FormatException(SYNTAX_MESSAGE);
        }
        Serialized strategy = StrategyEnumerator.instance()
            .parseCommandline(parts[0]);
        if (strategy == null) {
            throw new FormatException("Can't parse strategy %s", parts[0]);
        }
        Serialized acceptor = AcceptorEnumerator.instance()
            .parseCommandline(parts[1]);
        if (acceptor == null) {
            throw new FormatException("Can't parse acceptor %s", parts[1]);
        }
        int resultCount = 0;
        if (parts.length == 3) {
            String countMessage =
                String.format("Result count '%s' must be a non-negative number", parts[2]);
            try {
                resultCount = Integer.parseInt(parts[2]);
            } catch (NumberFormatException e) {
                throw new FormatException(countMessage);
            }
            if (resultCount < 0) {
                throw new FormatException(countMessage);
            }
        }
        return new ExploreType(strategy, acceptor, resultCount);
    }

    /** Message describing the syntax of a parsable exploration strategy. */
    static public final String SYNTAX_MESSAGE =
        "Exploration syntax: \"<strategy> <acceptor> [<resultcount>]\"";
    /** Default exploration (DFS, final states, infinite). */
    static public final ExploreType DEFAULT = new ExploreType();
}