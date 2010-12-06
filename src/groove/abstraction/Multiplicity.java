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
package groove.abstraction;

import groove.graph.Edge;
import groove.graph.Label;
import groove.graph.Node;

import java.util.HashSet;
import java.util.Set;

/**
 * A multiplicity is an approximation of the cardinality of a (finite) set. 
 * 
 * @author Eduardo Zambon
 */
public final class Multiplicity {

    // ------------------------------------------------------------------------
    // Static Fields
    // ------------------------------------------------------------------------

    private static final int OMEGA_VALUE = -1;

    /** The unbounded multiplicity. Differs from all other multiplicities. */
    public static final Multiplicity OMEGA = new Multiplicity(OMEGA_VALUE);

    private static Multiplicity STORE[];

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    /** Stores the multiplicity value. It is a natural number or omega. */
    private final int value;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Creates a multiplicity.
     * @param value - a natural number or omega.
     */
    private Multiplicity(int value) {
        assert (value >= 0 || value == OMEGA_VALUE) : "Multiplicities values must be natural numbers or omega.";
        this.value = value;
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    /** Two multiplicities are equal if they have the same value. */
    @Override
    public boolean equals(Object o) {
        boolean result;
        if (this == o) {
            result = true;
        } else if (!(o instanceof Multiplicity)) {
            result = false;
        } else {
            Multiplicity other = (Multiplicity) o;
            result = this.value == other.value;
        }
        // Check for consistency between equals and hashCode.
        assert (!result || this.hashCode() == o.hashCode());
        return result;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        return prime * this.value * this.value;
    }

    @Override
    public String toString() {
        String result;
        if (this.value == OMEGA_VALUE) {
            result = "w";
        } else {
            result = this.value + "";
        }
        return result;
    }

    // ------------------------------------------------------------------------
    // Static Methods
    // ------------------------------------------------------------------------

    /**
     * Initialises the multiplicity store so that multiplicity objects can be
     * reused. This method must be called as a preparation for using any other
     * abstraction classes in this package.
     */
    public static void initMultStore() {
        // We take a guess on the maximum number of multiplicities we are going
        // to need. We take 10 as a guess. This doesn't mean we can't create
        // multiplicities objects with value larger than 9, just that these
        // multiplicities are not stored.
        int multUpperBound = 10;
        STORE = new Multiplicity[multUpperBound];
        for (int i = 0; i < multUpperBound; i++) {
            STORE[i] = new Multiplicity(i);
        }
    }

    /** Returns the multiplicity object with the given value. */
    public static Multiplicity getMultOf(int value) {
        assert STORE != null : "The multiplicity store must be initialized first.";
        if (value >= 0 && value < STORE.length - 1) {
            return STORE[value];
        } else {
            return new Multiplicity(value);
        }
    }

    /**
     * Returns the multiplicity of the set of nodes given, bounded by the node
     * multiplicity bound (\nu) set in the Parameters class. 
     */
    public static Multiplicity getNodeSetMult(Set<? extends Node> nodes) {
        int setSize = nodes.size();
        int nodesMultBound = Parameters.getNodeMultBound();
        return getMult(setSize, nodesMultBound);
    }

    /**
     * Returns the multiplicity of the set of edges given, bounded by the edge
     * multiplicity bound (\mu) set in the Parameters class. 
     */
    public static Multiplicity getEdgeSetMult(Set<? extends Edge> edges) {
        int setSize = edges.size();
        int edgesMultBound = Parameters.getEdgeMultBound();
        return getMult(setSize, edgesMultBound);
    }

    /**
     * Computes the proper bounded multiplicity.
     * See Def. 5, pg 8 of Technical Report.
     * @param setSize - the cardinality of a set.
     * @param multBound - the multiplicity bound.
     * @return setSize, if setSize <= multBound;
     *         omega, otherwise.
     */
    public static Multiplicity getMult(int setSize, int multBound) {
        Multiplicity result;
        if (setSize <= multBound) {
            result = getMultOf(setSize);
        } else {
            result = OMEGA;
        }
        return result;
    }

    /** Returns true if both given sets have the same multiplicity. */
    public static <E extends Edge> boolean haveSameMult(Set<E> s0, Set<E> s1) {
        return getEdgeSetMult(s0).equals(getEdgeSetMult(s1));
    }

    /**
     * Returns the bounded sum of the outgoing multiplicities of the edge
     * signatures defined by the given node, label, and set of equivalence
     * classes. See item 6 of Def. 22 on page 17 of the Technical Report for
     * more details.
     */
    public static Multiplicity sumOutMult(Shape shape, ShapeNode node,
            Label label, Set<EquivClass<ShapeNode>> kSet) {
        Multiplicity accumulator = getMultOf(0);
        for (EquivClass<ShapeNode> k : kSet) {
            EdgeSignature es = shape.getEdgeSignature(node, label, k);
            Multiplicity out = shape.getEdgeSigOutMult(es);
            accumulator = accumulator.addEdgeMult(out);
        }
        return accumulator;
    }

    /**
     * Returns the bounded sum of the incoming multiplicities of the edge
     * signatures defined by the given node, label, and set of equivalence
     * classes. See item 6 of Def. 22 on page 17 of the Technical Report for
     * more details.
     */
    public static Multiplicity sumInMult(Shape shape, ShapeNode node,
            Label label, Set<EquivClass<ShapeNode>> kSet) {
        Multiplicity accumulator = getMultOf(0);
        for (EquivClass<ShapeNode> k : kSet) {
            EdgeSignature es = shape.getEdgeSignature(node, label, k);
            Multiplicity in = shape.getEdgeSigInMult(es);
            accumulator = accumulator.addEdgeMult(in);
        }
        return accumulator;
    }

    /**
     * Returns the bounded sum of the node multiplicities of the given set.
     * See item 5 of Def. 22 on page 17 of the Technical Report for
     * more details.
     */
    public static Multiplicity getNodeSetMultSum(Shape shape, Set<Node> nodes) {
        Multiplicity accumulator = getMultOf(0);
        for (Node node : nodes) {
            Multiplicity nodeMult = shape.getNodeMult((ShapeNode) node);
            accumulator = accumulator.addNodeMult(nodeMult);
        }
        return accumulator;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /**
     * Compare this multiplicity with the given parameter for order.
     * @param mult - the multiplicity to be compared with.
     * @return a negative integer, zero, or a positive integer if this
     *         multiplicity is less than, equal to, or greater than
     *         <code>mult</code>. This also considers omega.
     */
    public int compare(Multiplicity mult) {
        int result;
        if (this.value == mult.value) {
            result = 0;
        } else if (this.value == OMEGA_VALUE
            || (mult.value != OMEGA_VALUE && this.value > mult.value)) {
            result = 1;
        } else {
            result = -1;
        }
        return result;
    }

    /** Returns false if the multiplicity equals zero; true, otherwise. */
    public boolean isPositive() {
        return this.value == OMEGA_VALUE || this.value > 0;
    }

    /** Returns false if the multiplicity is at most one; true, otherwise. */
    public boolean isAbstract() {
        return !this.isAtMost(getMultOf(1));
    }

    /**
     * @param mult - the multiplicity to compare.
     * @return false if this is greater than mult; true, otherwise.
     */
    public boolean isAtMost(Multiplicity mult) {
        return this.compare(mult) != 1;
    }

    /** Returns the bounded sum of two multiplicities. */
    public Multiplicity add(Multiplicity mult, int bound) {
        assert bound > 0 : "Invalid multiplicity bound: " + bound;
        Multiplicity result;
        if (this.value == OMEGA_VALUE || mult.value == OMEGA_VALUE) {
            result = OMEGA;
        } else {
            result = getMult(this.value + mult.value, bound);
        }
        return result;
    }

    /** Returns the bounded sum of two node multiplicities. */
    public Multiplicity addNodeMult(Multiplicity mult) {
        return this.add(mult, Parameters.getNodeMultBound());
    }

    /** Returns the bounded sum of two edge multiplicities. */
    public Multiplicity addEdgeMult(Multiplicity mult) {
        return this.add(mult, Parameters.getEdgeMultBound());
    }

    /** Returns the unbounded sum of two multiplicities. */
    public Multiplicity uadd(Multiplicity mult) {
        return add(mult, Integer.MAX_VALUE);
    }

    /**
     * Subtracts mult from this.
     * @param mult - the value to be subtracted; required that mult <= this.
     * @param bound - the bound used in these multiplicities.
     * @return a set of multiplicities. There are two cases.
     *         - If this != omega, then the result is a singleton set with
     *           this.value - mult.value .
     *         - If this == omega, then the result is a set of multiplicities
     *           in the range (bound + 1 - mult, ..., omega) .
     */
    public Set<Multiplicity> sub(Multiplicity mult, int bound) {
        assert mult.isAtMost(this) : "Cannot subtract " + mult + " from "
            + this;
        Set<Multiplicity> result = new HashSet<Multiplicity>();
        if (this.value != OMEGA_VALUE) {
            result.add(getMultOf(this.value - mult.value));
        } else {
            int lowerBound;
            if (mult.value == OMEGA_VALUE) {
                lowerBound = 0;
            } else {
                lowerBound = bound + 1 - mult.value;
            }
            for (int i = lowerBound; i <= bound; i++) {
                result.add(getMultOf(i));
            }
            result.add(OMEGA);
        }
        return result;
    }

    /** Returns the subtraction of two node multiplicities. */
    public Set<Multiplicity> subNodeMult(Multiplicity mult) {
        return this.sub(mult, Parameters.getNodeMultBound());
    }

    /** Returns the subtraction of two edge multiplicities. */
    public Set<Multiplicity> subEdgeMult(Multiplicity mult) {
        return this.sub(mult, Parameters.getEdgeMultBound());
    }

    /** Returns the unbounded product of two multiplicities. */
    public Multiplicity multiply(Multiplicity mult) {
        int value = this.value * mult.value;
        if ((value < 0)
            || (this.value == OMEGA_VALUE && mult.value == OMEGA_VALUE)) {
            value = OMEGA_VALUE;
        }
        return getMultOf(value);
    }

    /** Checks if the both multiplicities have at least one value in common. */
    public boolean overlaps(Multiplicity mult) {
        boolean result;
        int bound = Parameters.getEdgeMultBound();

        if (this.value != OMEGA_VALUE && mult.value != OMEGA_VALUE) {
            // Both multiplicities have one precise value.
            result = this.value == mult.value;
        } else if (this.value != OMEGA_VALUE && mult.value == OMEGA_VALUE) {
            result = this.value > bound;
        } else if (this.value == OMEGA_VALUE && mult.value != OMEGA_VALUE) {
            result = mult.value > bound;
        } else {
            // Both multiplicities are omega.
            result = true;
        }
        return result;
    }
}
