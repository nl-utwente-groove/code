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
package nl.utwente.groove.explore.result;

import nl.utwente.groove.grammar.Action;
import nl.utwente.groove.grammar.Prover;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.GraphTransition;

/**
 * A <code>Predicate</code> over <code>A></code> is a boolean condition that
 * can be evaluated over objects of type <code>A</code>.
 * Special fields are maintained inside to record if the predicate can be
 * evaluated over graph states or graph transitions. This information is used
 * in the <code>PredicateAcceptor</code>.
 *
 * @see PredicateAcceptor
 * @author Maarten de Mol
 */
public abstract class Predicate<X> implements java.util.function.Predicate<X> {
    /**
     * Constructor for a state or transition predicate.
     *
     * @param forStates if {@code true}, this is a state predicate;
     * otherwise, it is a transition predicate.
     */
    protected Predicate(boolean forStates) {
        this.forStates = forStates;
    }

    /** Indicates that this predicate tests graph states.
     * If {@code false}, it tests rule transitions.
     */
    public boolean forStates() {
        return this.forStates;
    }

    private final boolean forStates;

    /** Predicate class for graph states. */
    abstract public static class StatePredicate extends Predicate<GraphState> {
        /** Constructor for subclassing. */
        protected StatePredicate() {
            super(true);
        }
    }

    /** Predicate class for rule transitions. */
    abstract public static class TransitionPredicate extends Predicate<GraphTransition> {
        /** Constructor for subclassing. */
        protected TransitionPredicate() {
            super(false);
        }
    }

    /**
     * <======================================================================>
     * Convenience class for defining the predicate !P.
     * <======================================================================>
     */
    public static class Not<X> extends Predicate<X> {
        private final Predicate<X> P;

        /** Default constructor. */
        public Not(Predicate<X> P) {
            super(P.forStates());
            this.P = P;
        }

        @Override
        public boolean test(X value) {
            return !this.P.test(value);
        }
    }

    /**
     * <======================================================================>
     * Convenience class for defining the predicate (P1 && P2 && .. && Pn).
     * <======================================================================>
     */
    public static class And<X> extends Predicate<X> {
        private final Predicate<X> P;
        private final Predicate<X> Q;

        /** Default constructor. */
        public And(Predicate<X> P, Predicate<X> Q) {
            super(P.forStates());
            assert P.forStates() == Q.forStates();
            this.P = P;
            this.Q = Q;
        }

        @Override
        public boolean test(X value) {
            return this.P.test(value) && this.Q.test(value);
        }
    }

    /**
     * <======================================================================>
     * Convenience class for defining the predicate (P1 || P2 || ... || Pn).
     * <======================================================================>
     */
    public static class Or<X> extends Predicate<X> {
        private final Predicate<X> P;
        private final Predicate<X> Q;

        /** Default constructor. */
        public Or(Predicate<X> P, Predicate<X> Q) {
            super(P.forStates());
            assert P.forStates() == Q.forStates();
            this.P = P;
            this.Q = Q;
        }

        @Override
        public boolean test(X value) {
            return this.P.test(value) || this.Q.test(value);
        }
    }

    /**
     * <======================================================================>
     * Convenience class for defining the predicate (P -> Q).
     * <======================================================================>
     */
    public static class Implies<X> extends Predicate<X> {
        private final Predicate<X> P;
        private final Predicate<X> Q;

        /** Default constructor. */
        public Implies(Predicate<X> P, Predicate<X> Q) {
            super(P.forStates());
            assert P.forStates() == Q.forStates();
            this.P = P;
            this.Q = Q;
        }

        @Override
        public boolean test(X value) {
            if (this.P.test(value)) {
                return this.Q.test(value);
            } else {
                return true;
            }
        }
    }

    /**
     * <======================================================================>
     * Convenience class for defining the predicate on graph states that
     * checks whether a given rule is applicable.
     * <======================================================================>
     */
    public static class RuleApplicable extends StatePredicate {
        /** Default constructor. */
        public RuleApplicable(Rule rule) {
            this.prover = rule.getProver();
        }

        private final Prover prover;

        @Override
        public boolean test(GraphState value) {
            return this.prover.hasProof(value.getGraph());
        }
    }

    /**
     * <======================================================================>
     * Convenience class for defining the predicate on graph transitions that
     * checks whether a given rule has been applied.
     * <======================================================================>
     */
    public static class ActionApplied extends TransitionPredicate {
        private final Action action;

        /** Default constructor. */
        public ActionApplied(Action action) {
            this.action = action;
        }

        @Override
        public boolean test(GraphTransition value) {
            return value.getAction().equals(this.action);
        }
    }
}
