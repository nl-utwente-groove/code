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

import nl.utwente.groove.explore.encode.Serialized;
import nl.utwente.groove.explore.result.Acceptor;
import nl.utwente.groove.explore.result.CycleAcceptor;
import nl.utwente.groove.explore.strategy.Boundary;
import nl.utwente.groove.explore.strategy.BoundedLTLStrategy;
import nl.utwente.groove.explore.strategy.BoundedPocketLTLStrategy;
import nl.utwente.groove.explore.strategy.LTLStrategy;
import nl.utwente.groove.explore.strategy.Strategy;
import nl.utwente.groove.grammar.Grammar;

/**
 * Exploration type for LTL model checking: a nested depth-first search for a
 * counterexample to a given LTL property, with the cycle acceptor. The
 * strategy and acceptor are instantiated directly, without the
 * encode/enumerator machinery; the legacy descriptors of the base class
 * serve display purposes only.
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
                + "using incremental bounds based on graph size or rule applications"),
        /** Bounded search avoiding the re-exploration of connected components. */
        POCKET("ltlpocket",
            "Nested Depth-First Search for a given LTL formula,"
                + "using incremental bounds based on graph size or rule applications"
                + "and optimised to avoid reexploring connected components ('pockets')"),;

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
     * Constructs an LTL exploration type of a given flavour.
     * @param kind the model-checking flavour
     * @param property the LTL property to be checked
     * @param boundary the exploration boundary; must be non-{@code null}
     * exactly for the bounded flavours
     */
    public LTLExploreType(Kind kind, String property, Boundary boundary) {
        super(createStrategyDescriptor(kind, property), new Serialized("cycle"), 1);
        assert (boundary == null) == (kind == Kind.PLAIN);
        this.kind = kind;
        this.property = property;
        this.boundary = boundary;
    }

    private final Kind kind;
    private final String property;
    private final Boundary boundary;

    /** Computes the legacy display descriptor for a given flavour and property. */
    private static Serialized createStrategyDescriptor(Kind kind, String property) {
        Serialized result = new Serialized(kind.getKeyword());
        result.setArgument("prop", property);
        return result;
    }

    @Override
    public Strategy getParsedStrategy(Grammar grammar) {
        LTLStrategy result = switch (this.kind) {
        case PLAIN -> new LTLStrategy();
        case BOUNDED -> new BoundedLTLStrategy();
        case POCKET -> new BoundedPocketLTLStrategy();
        };
        result.setProperty(this.property);
        if (result instanceof BoundedLTLStrategy bounded) {
            bounded.setBoundary(this.boundary);
        }
        return result;
    }

    @Override
    public Acceptor getParsedAcceptor(Grammar grammar) {
        return CycleAcceptor.PROTOTYPE;
    }
}
