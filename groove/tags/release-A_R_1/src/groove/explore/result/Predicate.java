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
package groove.explore.result;

import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.Rule;

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
public abstract class Predicate<A> {

    /** Indicator whether the generic type A equals GraphState. */
    public final boolean statePredicate;
    /** Indicator whether the generic type A equals GraphTransition. */
    public final boolean transitionPredicate;

    /**
     * Constructor for explicitly setting the final fields.
     */
    public Predicate(boolean statePredicate, boolean transitionPredicate) {
        this.statePredicate = statePredicate;
        this.transitionPredicate = transitionPredicate;
    }

    /**
     * Convenience constructor.
     */
    public Predicate() {
        this(false, false);
    }

    /**
     * The evaluation method of the predicate.
     */
    public abstract boolean eval(A value);

    /**
     * <======================================================================>
     * Convenience class for defining the predicate !P.
     * <======================================================================>
     */
    public static class Not<X> extends Predicate<X> {
        private final Predicate<X> P;

        /** Default constructor. */
        public Not(Predicate<X> P) {
            super(P.statePredicate, P.transitionPredicate);
            this.P = P;
        }

        @Override
        public boolean eval(X value) {
            return !this.P.eval(value);
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
            super(P.statePredicate, P.transitionPredicate);
            this.P = P;
            this.Q = Q;
        }

        @Override
        public boolean eval(X value) {
            return this.P.eval(value) && this.Q.eval(value);
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
            super(P.statePredicate, P.transitionPredicate);
            this.P = P;
            this.Q = Q;
        }

        @Override
        public boolean eval(X value) {
            return this.P.eval(value) || this.Q.eval(value);
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
            super(P.statePredicate, P.transitionPredicate);
            this.P = P;
            this.Q = Q;
        }

        @Override
        public boolean eval(X value) {
            if (this.P.eval(value)) {
                return this.Q.eval(value);
            } else {
                return true;
            }
        }
    }

    /**
     * <======================================================================>
     * Convenience class for defining the predicate (P <-> Q).
     * <======================================================================>
     */
    public static class Iff<X> extends Predicate<X> {
        private final Predicate<X> P;
        private final Predicate<X> Q;

        /** Default constructor. */
        public Iff(Predicate<X> P, Predicate<X> Q) {
            super(P.statePredicate, P.transitionPredicate);
            this.P = P;
            this.Q = Q;
        }

        @Override
        public boolean eval(X value) {
            return (this.P.eval(value) == this.Q.eval(value));
        }
    }

    /**
     * <======================================================================>
     * Convenience class for defining the predicate on graph states that
     * checks whether a given rule is applicable.
     * <======================================================================>
     */
    public static class RuleApplicable extends Predicate<GraphState> {
        private final Rule rule;

        /** Default constructor. */
        public RuleApplicable(Rule rule) {
            super(true, false);
            this.rule = rule;
        }

        @Override
        public boolean eval(GraphState value) {
            return this.rule.hasMatch(value.getGraph());
        }
    }

    /**
     * <======================================================================>
     * Convenience class for defining the predicate on graph transitions that
     * checks whether a given rule has been applied.
     * <======================================================================>
     */
    public static class RuleApplied extends Predicate<GraphTransition> {
        private final Rule rule;

        /** Default constructor. */
        public RuleApplied(Rule rule) {
            super(false, true);
            this.rule = rule;
        }

        @Override
        public boolean eval(GraphTransition value) {
            return value.getEvent().getRule().equals(this.rule);
        }
    }

}
