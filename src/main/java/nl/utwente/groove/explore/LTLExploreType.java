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

import nl.utwente.groove.explore.config.parse.BoundaryParser;
import nl.utwente.groove.explore.result.Acceptor;
import nl.utwente.groove.explore.result.CycleAcceptor;
import nl.utwente.groove.explore.strategy.Boundary;
import nl.utwente.groove.explore.strategy.BoundedLTLStrategy;
import nl.utwente.groove.explore.strategy.BoundedPocketLTLStrategy;
import nl.utwente.groove.explore.strategy.LTLStrategy;
import nl.utwente.groove.explore.strategy.Strategy;
import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Exploration type for LTL model checking: a nested depth-first search for a
 * counterexample to a given LTL property, with the cycle acceptor. The
 * strategy and acceptor are instantiated directly from the state of the
 * type.
 * @author Arend Rensink
 * @version $Revision$
 */
public class LTLExploreType extends ExploreType {
    /** The flavours of LTL model-checking exploration. */
    public enum Kind {
        /** Plain nested depth-first search. */
        PLAIN("ltl", "Nested Depth-First Search for a given LTL formula."),
        /** Search with incremental bounds on graph size or rule applications. */
        BOUNDED("ltlbounded",
            "Nested Depth-First Search for a given LTL formula,"
                + " using incremental bounds based on graph size or rule applications"),
        /** Bounded search avoiding the re-exploration of connected components. */
        POCKET("ltlpocket",
            "Nested Depth-First Search for a given LTL formula,"
                + " using incremental bounds based on graph size or rule applications"
                + " and optimised to avoid reexploring connected components ('pockets')"),;

        private Kind(String keyword, String description) {
            this.keyword = keyword;
            this.description = description;
        }

        /** Returns the identifying keyword of this flavour. */
        public String getKeyword() {
            return this.keyword;
        }

        private final String keyword;

        /** Returns a description of this flavour. */
        public String getDescription() {
            return this.description;
        }

        private final String description;
    }

    /**
     * Constructs an LTL exploration type of a given flavour, stopping at the
     * first counterexample.
     * @param kind the model-checking flavour
     * @param property the LTL property to be checked
     * @param boundary the exploration boundary; must be non-{@code null}
     * exactly for the bounded flavours
     */
    public LTLExploreType(Kind kind, String property, Boundary boundary) {
        this(kind, property, boundary, null, 1);
    }

    /**
     * Constructs an LTL exploration type of a given flavour from a textual
     * boundary specification, which is resolved against the grammar when the
     * strategy is instantiated.
     * @param kind the model-checking flavour
     * @param property the LTL property to be checked
     * @param boundarySpec the boundary specification (see
     * {@link BoundaryParser}); must be non-{@code null} exactly for the
     * bounded flavours
     * @param count number of results after which exploration halts;
     * {@code 0} means unbounded
     */
    public LTLExploreType(Kind kind, String property, String boundarySpec, int count) {
        this(kind, property, null, boundarySpec, count);
    }

    private LTLExploreType(Kind kind, String property, Boundary boundary, String boundarySpec,
                           int count) {
        super(count);
        assert kind == Kind.PLAIN
            ? boundary == null && boundarySpec == null
            : boundary != null ^ boundarySpec != null;
        this.kind = kind;
        this.property = property;
        this.boundary = boundary;
        this.boundarySpec = boundarySpec;
    }

    private final Kind kind;
    private final String property;
    private final Boundary boundary;
    private final String boundarySpec;

    @Override
    public String getIdentifier() {
        StringBuilder result = new StringBuilder(this.kind.getKeyword());
        result.append(':');
        if (this.kind != Kind.PLAIN) {
            result
                .append(this.boundarySpec == null
                    ? this.boundary.toString()
                    : this.boundarySpec);
            result.append(';');
        }
        result.append(this.property);
        return result.toString();
    }

    @Override
    public Strategy getParsedStrategy(Grammar grammar) throws FormatException {
        LTLStrategy result = switch (this.kind) {
        case PLAIN -> new LTLStrategy();
        case BOUNDED -> new BoundedLTLStrategy();
        case POCKET -> new BoundedPocketLTLStrategy();
        };
        result.setProperty(this.property);
        if (result instanceof BoundedLTLStrategy bounded) {
            bounded
                .setBoundary(this.boundary == null
                    ? BoundaryParser.parse(grammar, this.boundarySpec)
                    : this.boundary);
        }
        return result;
    }

    @Override
    public Acceptor getParsedAcceptor(Grammar grammar) {
        return CycleAcceptor.PROTOTYPE;
    }

    @Override
    public ExploreType withResultCount(int count) {
        return new LTLExploreType(this.kind, this.property, this.boundary, this.boundarySpec,
            count);
    }
}
